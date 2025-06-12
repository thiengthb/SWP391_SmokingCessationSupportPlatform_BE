package com.swpteam.smokingcessation.schedule;

import com.swpteam.smokingcessation.apis.account.Account;
import com.swpteam.smokingcessation.apis.mail.MailService;
import com.swpteam.smokingcessation.apis.message.Message;
import com.swpteam.smokingcessation.apis.message.MessageRepository;
import com.swpteam.smokingcessation.apis.setting.Setting;
import com.swpteam.smokingcessation.apis.setting.SettingRepository;
import com.swpteam.smokingcessation.apis.setting.enums.MotivationFrequency;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class Reminder {

    private final MessageRepository messageRepository;
    private final MailService mailService;
    private final SettingRepository settingRepository;
    private final Random random = new Random();

    // REMINDER DEADLINE - Gửi 30 phút trước deadline
    @Scheduled(cron = "0 * * * * *") // Chạy mỗi phút
    public void sendReminders() {
        LocalTime currentTime = LocalTime.now().withSecond(0).withNano(0);
        LocalTime deadlineIn30Minutes = currentTime.plusMinutes(30);

        List<Setting> settings = settingRepository.findByReportDeadline(deadlineIn30Minutes);

        for (Setting setting : settings) {
            try {
                Account account = setting.getAccount();
                String userEmail = account.getEmail();
                mailService.sendReminderMail(userEmail);
            } catch (Exception e) {
                log.error("Failed to send reminder for setting accountId: {}", setting.getAccountId(), e);
            }
        }
    }

    // MOTIVATION MESSAGES - Daily (8AM)
    @Scheduled(cron = "0 0 8 * * *") // 8AM every day
    public void sendDailyMotivation() {
        sendMotivation(MotivationFrequency.DAILY);
    }

    // MOTIVATION MESSAGES - Every 6 Hours (8AM, 14PM, 20PM)
    @Scheduled(cron = "0 0 8,14,20,0 * * *") // Every 6 hours
    public void sendEvery6HoursMotivation() {
        sendMotivation(MotivationFrequency.EVERY6HOURS);
    }

    // MOTIVATION MESSAGES - Every 12 Hours (8AM, 20PM)
    @Scheduled(cron = "0 0 8,20 * * *") // 8AM and 8PM
    public void sendEvery12HoursMotivation() {
        sendMotivation(MotivationFrequency.EVERY12HOURS);
    }

    // MOTIVATION MESSAGES - Weekly (Sunday 8AM)
    @Scheduled(cron = "0 0 8 * * SUN") // 8AM every Sunday
    public void sendWeeklyMotivation() {
        sendMotivation(MotivationFrequency.WEEKLY);
    }

    // MOTIVATION MESSAGES - Monthly (1st day of month, 8AM)
    @Scheduled(cron = "0 0 8 1 * *") // 8AM on 1s   t day of every month
    public void sendMonthlyMotivation() {
        sendMotivation(MotivationFrequency.MONTHLY);
    }

    private void sendMotivationMessages(List<Setting> settings) {

        int successCount = 0;
        for (Setting setting : settings) {
            try {
                Account account = setting.getAccount();
                String userEmail = account.getEmail();

                // Get random motivation message
                Message randomMotivationMessage = getRandomMotivationMessage();

                // Send motivation email
                mailService.sendMotivationMail(userEmail, randomMotivationMessage);

            } catch (Exception e) {
                log.error("Failed to send motivation for setting accountId: {}"
                        , setting.getAccountId(), e);
            }
        }
    }

    private Message getRandomMotivationMessage() {
        List<Message> motivationMessages = messageRepository.IsDeletedFalse();
        int randomIndex = random.nextInt(motivationMessages.size());
        return motivationMessages.get(randomIndex);
    }

    private void sendMotivation(MotivationFrequency motivationFrequency) {

        List<Setting> dailySettings = settingRepository.findByMotivationFrequency(motivationFrequency);

        if (dailySettings.isEmpty()) {
            log.debug("No users found with DAILY motivation setting");
            return;
        }
        sendMotivationMessages(dailySettings);
    }
}