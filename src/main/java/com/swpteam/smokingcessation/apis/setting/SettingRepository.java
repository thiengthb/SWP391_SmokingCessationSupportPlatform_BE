package com.swpteam.smokingcessation.apis.setting;

import com.swpteam.smokingcessation.apis.setting.enums.MotivationFrequency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface SettingRepository extends JpaRepository<Setting, String>  {
    @Query("SELECT s FROM Setting s WHERE s.reportDeadline = :deadlineTime")
    List<Setting> findByReportDeadline(@Param("deadlineTime") LocalTime deadlineTime);

    List<Setting> findByMotivationFrequency(MotivationFrequency motivationFrequency);
}
