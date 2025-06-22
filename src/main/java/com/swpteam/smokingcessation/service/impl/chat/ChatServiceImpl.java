package com.swpteam.smokingcessation.service.impl.chat;

import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.dto.chat.ChatRequest;
import com.swpteam.smokingcessation.domain.dto.chat.ChatResponse;
import com.swpteam.smokingcessation.domain.dto.chat.ChatRestResponse;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Chat;
import com.swpteam.smokingcessation.domain.mapper.ChatMapper;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.repository.AccountRepository;
import com.swpteam.smokingcessation.repository.ChatRepository;
import com.swpteam.smokingcessation.service.interfaces.chat.IChatService;
import com.swpteam.smokingcessation.service.interfaces.identity.IAccountService;
import com.swpteam.smokingcessation.utils.AuthUtilService;
import com.swpteam.smokingcessation.utils.ValidationUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ChatServiceImpl implements IChatService {

    ChatMapper chatMapper;
    ChatRepository chatRepository;
    IAccountService accountService;
    AuthUtilService authUtilService;

    @Override
    public ChatResponse sendChatMessage(ChatRequest request) {
        Account account = accountService.findAccountByIdOrThrowError(request.accountId());

        boolean isFirstTime = !chatRepository.existsByAccountIdAndIsDeletedFalse(request.accountId());

        Chat chat = Chat.builder()
                .account(account)
                .content(request.content())
                .build();

        chatRepository.save(chat);

        ChatResponse chatResponse = chatMapper.toResponse(chat);
        chatResponse.setFirstTime(isFirstTime);
        return chatResponse;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Page<ChatRestResponse> getChats(PageableRequest request) {
        ValidationUtil.checkFieldExist(Chat.class, request.sortBy());

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Chat> chats = chatRepository.findAllByIsDeletedFalse(pageable);

        return chats.map(chatMapper::toRestResponse);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Page<ChatRestResponse> getChatsById(String id, PageableRequest request) {
        ValidationUtil.checkFieldExist(Chat.class, request.sortBy());

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Chat> chats = chatRepository.findByAccountIdAndIsDeletedFalse(id, pageable);

        return chats.map(chatMapper::toRestResponse);
    }

    @Override
    public void softDeleteChat(String id) {
        Chat chat = chatRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.CHAT_NOT_FOUND));

        boolean haveAccess = authUtilService.isAdminOrOwner(chat.getAccount().getId());
        if (!haveAccess) {
            throw new AppException(ErrorCode.OTHERS_CHAT_CANNOT_BE_DELETED);
        }

        chat.setDeleted(true);
        chatRepository.save(chat);
    }

}
