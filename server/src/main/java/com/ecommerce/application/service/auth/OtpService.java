package com.ecommerce.application.service.auth;

import com.ecommerce.application.port.in.otp.OtpUseCase;
import com.ecommerce.application.port.out.notification.NotificationChannel;
import com.ecommerce.application.port.out.notification.NotificationPort;
import com.ecommerce.application.port.out.notification.NotificationRequest;
import com.ecommerce.application.port.out.persistence.OtpPort;
import com.ecommerce.domain.auth.Otp;
import com.ecommerce.domain.exception.InvalidOtpException;
import com.ecommerce.domain.exception.OtpExpiredException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OtpService implements OtpUseCase {

    private final NotificationPort notificationPort;
    private final OtpPort otpPort;

    @Override
    public void generate(String email) {
        Otp otp = Otp.generate();
        otpPort.save(email, otp);

        notificationPort.send(NotificationRequest.builder()
                .channel(NotificationChannel.EMAIL)
                .recipient(email)
                .subject("Your Verification OTP")
                .content("Your OTP code is: <b>" + otp.getCode() + "</b>. It will expire in 5 minutes.")
                .build());
    }

    @Override
    public void verify(String email, String inputCode) {
        Otp otp = otpPort.findActiveByEmail(email)
                .orElseThrow(() -> new InvalidOtpException("No active OTP found for: " + email));

        if (otp.isExpired()) {
            throw new OtpExpiredException();
        }

        if (!otp.isValid(inputCode)) {
            throw new InvalidOtpException();
        }

        otpPort.markAsUsed(email, inputCode);
        otpPort.deleteVerifiedUser(email);
    }
}
