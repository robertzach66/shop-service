package com.shop.notification.service;

import com.shop.order.event.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final JavaMailSender mailSender;

    @KafkaListener(topics = "order-placed")
    public void listen(OrderPlacedEvent orderPlacedEvent) {
        log.info("Got message from topic order-placed: {}", orderPlacedEvent);
        MimeMessagePreparator mimeMessagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom("info@shop.com");
            messageHelper.setTo(orderPlacedEvent.getEmail().toString());
            messageHelper.setSubject(String.format("Order %s placed successfully!", orderPlacedEvent.getOrderNumber()));
            messageHelper.setText(String.format("""
                    Hi %s %s,
                    
                    Your order with ordernumber: %s has been placed successfully!
                    
                    Best Regards
                    Your shop.com Team
                    """, orderPlacedEvent.getFirstName().toString(), orderPlacedEvent.getLastName().toString(), orderPlacedEvent.getOrderNumber().toString()
            ));
        };
        try {
            mailSender.send(mimeMessagePreparator);
            log.info("Order Notification email send!");
        } catch (MailException e) {
            log.error("Failed to send mail to {}. Reason: {}", orderPlacedEvent.getEmail(), e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
