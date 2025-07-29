package com.swpteam.smokingcessation.websocket;

import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.dto.account.AccountResponse;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.enums.AccountStatus;
import com.swpteam.smokingcessation.domain.mapper.AccountMapper;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.repository.jpa.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class WebSocketPresenceEventListener {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountMapper accountMapper;

    @Autowired
    SimpMessagingTemplate messagingTemplate;

    // Map sessionId -> accountId
    private final Map<String, String> sessionAccountMap = new ConcurrentHashMap<>();

    // Track accountId -> Set<sessionId>
    private final Map<String, ConcurrentHashMap.KeySetView<String, Boolean>> accountSessions = new ConcurrentHashMap<>();

    @EventListener
    @Transactional
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String accountId = accessor.getFirstNativeHeader("accountId");
        String sessionId = accessor.getSessionId();
        if (accountId != null && sessionId != null) {
            accountSessions.computeIfAbsent(accountId, k -> ConcurrentHashMap.newKeySet()).add(sessionId);
            sessionAccountMap.put(sessionId, accountId);
            Account account = accountRepository.findByIdAndIsDeletedFalse(accountId)
                    .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
            account.setStatus(AccountStatus.ONLINE);
            accountRepository.save(account);
            broadcastOnlineUser();
            log.info("Account {} connected (session: {})", accountId, sessionId);
        }
    }

    @EventListener
    @Transactional
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        String accountId = sessionAccountMap.remove(sessionId);
        if (accountId != null) {
            ConcurrentHashMap.KeySetView<String, Boolean> sessions = accountSessions.get(accountId);
            sessions.remove(sessionId);
            if (sessions.isEmpty()) {
                accountSessions.remove(accountId);
                accountRepository.findByIdAndIsDeletedFalse(accountId).ifPresent(account -> {
                    account.setStatus(AccountStatus.OFFLINE);
                    accountRepository.save(account);
                    broadcastOnlineUser();
                    log.info("Account {} is now OFFLINE", accountId);
                });
            }
        }
    }

    private void broadcastOnlineUser() {
        List<Account> onlineUsers = accountRepository.findAllByStatusAndIsDeletedFalse(AccountStatus.ONLINE);
        List<AccountResponse> responses = onlineUsers.stream()
                .map(accountMapper::toResponse)
                .toList();

        messagingTemplate.convertAndSend("/topic/online-users", responses);
    }
}
