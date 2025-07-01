package com.swpteam.smokingcessation.feature.version1.chat.service.impl;

import com.swpteam.smokingcessation.common.PageResponse;
import com.swpteam.smokingcessation.common.PageableRequest;
import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.dto.chat.ChatRequest;
import com.swpteam.smokingcessation.domain.dto.chat.ChatResponse;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.domain.entity.Chat;
import com.swpteam.smokingcessation.domain.mapper.ChatMapper;
import com.swpteam.smokingcessation.exception.AppException;
import com.swpteam.smokingcessation.repository.jpa.ChatRepository;
import com.swpteam.smokingcessation.feature.version1.chat.service.IChatService;
import com.swpteam.smokingcessation.feature.version1.identity.service.IAccountService;
import com.swpteam.smokingcessation.utils.AuthUtilService;
import com.swpteam.smokingcessation.utils.ValidationUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

        Chat chat = Chat.builder()
                .account(account)
                .content(request.content())
                .build();

        chatRepository.save(chat);

        ChatResponse chatResponse = chatMapper.toResponse(chat);
        return chatResponse;
    }

    @Override
    public PageResponse<ChatResponse> getChats(PageableRequest request) {
        ValidationUtil.checkFieldExist(Chat.class, request.sortBy());

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Chat> chats = chatRepository.findAllByIsDeletedFalse(pageable);

        return new PageResponse<>(chats.map(chatMapper::toResponse));
    }

    @Override
    public PageResponse<ChatResponse> getChatsById(String id, PageableRequest request) {
        ValidationUtil.checkFieldExist(Chat.class, request.sortBy());

        Pageable pageable = PageableRequest.getPageable(request);
        Page<Chat> chats = chatRepository.findByAccountIdAndIsDeletedFalse(id, pageable);

        return new PageResponse<>(chats.map(chatMapper::toResponse));
    }

    @Override
    public void softDeleteChat(String id) {
        Chat chat = chatRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.CHAT_NOT_FOUND));

        boolean haveAccess = authUtilService.isAdminOrOwner(chat.getAccount().getId());
        if (!haveAccess) {
            throw new AppException(ErrorCode.CHAT_DELETION_NOT_ALLOWED);
        }

        chat.setDeleted(true);
        chatRepository.save(chat);
    }

}
