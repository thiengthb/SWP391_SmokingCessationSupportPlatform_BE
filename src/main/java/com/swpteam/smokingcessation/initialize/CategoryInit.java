package com.swpteam.smokingcessation.initialize;

import com.swpteam.smokingcessation.constant.App;
import com.swpteam.smokingcessation.domain.entity.Category;
import com.swpteam.smokingcessation.repository.CategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryInit implements CommandLineRunner  {

    CategoryRepository categoryRepository;

    @Override
    public void run(String... args) {
        if (categoryRepository.count() == 0 ||
                categoryRepository.findByName(App.DEFAULT_CATEGORY).isEmpty())
        {
            Category uncategorized = Category.builder()
                    .name(App.DEFAULT_CATEGORY)
                    .build();

            categoryRepository.save(uncategorized);
            log.info("Default category has been created");
        }
    }

}

