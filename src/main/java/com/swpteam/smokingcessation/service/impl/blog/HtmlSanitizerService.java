package com.swpteam.smokingcessation.service.impl.blog;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.stereotype.Service;

@Service
public class HtmlSanitizerService {

    private final PolicyFactory policy = new HtmlPolicyBuilder()
            .allowElements("p", "b", "i", "u", "strong", "em", "ul", "ol", "li", "a", "br", "img")
            .allowUrlProtocols("https")
            .allowAttributes("href").onElements("a")
            .allowAttributes("src", "alt").onElements("img")
            .toFactory();

    public String sanitize(String htmlContent) {
        return policy.sanitize(htmlContent);
    }
    
}
