package com.swpteam.smokingcessation.initialize;

import com.swpteam.smokingcessation.domain.entity.Message;
import com.swpteam.smokingcessation.repository.jpa.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MotivationInit implements CommandLineRunner {

    private final MessageRepository messageRepository;

    @Override
    public void run(String... args) {
        if (messageRepository.count() == 0) {
            List<Message> messages = List.of(
                    Message.builder().content("Bạn có thể làm được! Hãy kiên trì từng ngày.").build(),
                    Message.builder().content("Mỗi ngày không hút thuốc là một chiến thắng mới!").build(),
                    Message.builder().content("Hãy nghĩ về sức khỏe và những người thân yêu của bạn.").build(),
                    Message.builder().content("Quyết tâm hôm nay là thành công ngày mai!").build(),
                    Message.builder().content("You are stronger than your cravings!").build(),
                    Message.builder().content("Every smoke-free day is a victory.").build(),
                    Message.builder().content("Stay positive, your health is worth it!").build(),
                    Message.builder().content("Keep going, you’re doing great!").build()
            );
            messageRepository.saveAll(messages);
        }
    }
}