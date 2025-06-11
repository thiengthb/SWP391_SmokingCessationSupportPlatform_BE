// ReminderService.java - Complete Implementation
package com.swpteam.smokingcessation.apis.mail;

import com.swpteam.smokingcessation.apis.account.entity.Account;
import com.swpteam.smokingcessation.apis.message.Message;
import com.swpteam.smokingcessation.apis.message.enums.MessageType;
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
public class ReminderService {

    private final MessageRepository messageRepository;
    private final MailService mailService;
    private final SettingRepository settingRepository;
    private final Random random = new Random();

    // REMINDER DEADLINE - Gửi 30 phút trước deadline
    @Scheduled(cron = "0 * * * * *") // Chạy mỗi phút
    public void sendReminders() {
        LocalTime currentTime = LocalTime.now().withSecond(0).withNano(0);
        LocalTime deadlineIn30Minutes = currentTime.plusMinutes(30);

        log.info("Checking reminders for current time: {}, deadline in 30 minutes: {}", currentTime, deadlineIn30Minutes);

        List<Setting> settings = settingRepository.findByReportDeadline(deadlineIn30Minutes);

        if (settings.isEmpty()) {
            log.debug("No settings found for deadline time: {}", deadlineIn30Minutes);
            return;
        }

        List<Message> reminderMessages = messageRepository.findByTypeAndIsDeletedFalse(MessageType.REMINDER);

        if (reminderMessages.isEmpty()) {
            log.warn("No reminder message found!");
            return;
        }

        Message reminderMessage = reminderMessages.get(0);

        int successCount = 0;
        for (Setting setting : settings) {
            try {
                Account account = setting.getAccount();
                String userEmail = account.getEmail();
                mailService.sendReminderMail(userEmail, reminderMessage);
                log.info("Sent 30-minute reminder to: {} for upcoming deadline: {}", userEmail, setting.getReportDeadline());
                successCount++;
            } catch (Exception e) {
                log.error("Failed to send reminder for setting accountId: {}", setting.getAccountId(), e);
            }
        }

        log.info("Successfully sent {}/{} reminders 30 minutes before deadline", successCount, settings.size());
    }

    // MOTIVATION MESSAGES - Daily (8AM)
    @Scheduled(cron = "0 0 8 * * *") // 8AM every day
    public void sendDailyMotivation() {
        log.info("Starting daily motivation sending at: {}", LocalDateTime.now());

        List<Setting> dailySettings = settingRepository.findByMotivationFrequency(MotivationFrequency.DAILY);

        if (dailySettings.isEmpty()) {
            log.debug("No users found with DAILY motivation setting");
            return;
        }

        sendMotivationMessages(dailySettings);
    }

    // MOTIVATION MESSAGES - Every 6 Hours (8AM, 14PM, 20PM)
    @Scheduled(cron = "0 0 8,14,20,0 * * *") // Every 6 hours
    public void sendEvery6HoursMotivation() {
        log.info("Starting 6-hourly motivation sending at: {}", LocalDateTime.now());

        List<Setting> every6HoursSettings = settingRepository.findByMotivationFrequency(MotivationFrequency.EVERY6HOURS);

        if (every6HoursSettings.isEmpty()) {
            log.debug("No users found with EVERY6HOURS motivation setting");
            return;
        }

        sendMotivationMessages(every6HoursSettings);
    }

    // MOTIVATION MESSAGES - Every 12 Hours (8AM, 20PM)
    @Scheduled(cron = "0 0 8,20 * * *") // 8AM and 8PM
    public void sendEvery12HoursMotivation() {
        log.info("Starting 12-hourly motivation sending at: {}", LocalDateTime.now());

        List<Setting> every12HoursSettings = settingRepository.findByMotivationFrequency(MotivationFrequency.EVERY12HOURS);

        if (every12HoursSettings.isEmpty()) {
            log.debug("No users found with EVERY12HOURS motivation setting");
            return;
        }

        sendMotivationMessages(every12HoursSettings);
    }

    // MOTIVATION MESSAGES - Weekly (Sunday 8AM)
    @Scheduled(cron = "0 0 8 * * SUN") // 8AM every Sunday
    public void sendWeeklyMotivation() {
        log.info("Starting weekly motivation sending at: {}", LocalDateTime.now());

        List<Setting> weeklySettings = settingRepository.findByMotivationFrequency(MotivationFrequency.WEEKLY);

        if (weeklySettings.isEmpty()) {
            log.debug("No users found with WEEKLY motivation setting");
            return;
        }

        sendMotivationMessages(weeklySettings);
    }

    // MOTIVATION MESSAGES - Monthly (1st day of month, 8AM)
    @Scheduled(cron = "0 0 8 1 * *") // 8AM on 1s   t day of every month
    public void sendMonthlyMotivation() {
        log.info("Starting monthly motivation sending at: {}", LocalDateTime.now());

        List<Setting> monthlySettings = settingRepository.findByMotivationFrequency(MotivationFrequency.MONTHLY);

        if (monthlySettings.isEmpty()) {
            log.debug("No users found with MONTHLY motivation setting");
            return;
        }

        sendMotivationMessages(monthlySettings);
    }

    /**
     * Helper method to send motivation messages to a list of settings
     */
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

                log.info("Sent motivation to: {} with message: '{}'",
                      userEmail, truncateMessage(randomMotivationMessage.getContent()));
                successCount++;

            } catch (Exception e) {
                log.error("Failed to send motivation for setting accountId: {}"
                        , setting.getAccountId(), e);
            }
        }

        log.info("Successfully sent {}/{} motivation messages", successCount, settings.size());
    }

    /**
     * Get random motivation message from the list
     */
    private Message getRandomMotivationMessage() {
        List<Message> motivationMessages = messageRepository.findByTypeAndIsDeletedFalse(MessageType.MOTIVATION);
        int randomIndex = random.nextInt(motivationMessages.size());
        return motivationMessages.get(randomIndex);
    }

    /**
     * Truncate message content for logging (first 50 characters)
     */
    private String truncateMessage(String content) {
        if (content == null) return "null";
        return content.length() > 50 ? content.substring(0, 50) + "..." : content;
    }
}