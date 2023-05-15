package sample.cafekiosk.spring.api.service.mail;

import org.assertj.core.api.Assertions;
import org.hibernate.service.spi.InjectService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import sample.cafekiosk.spring.client.MailSendClient;
import sample.cafekiosk.spring.domain.hisory.mail.MailSendHistory;
import sample.cafekiosk.spring.domain.hisory.mail.MailSendHistoryRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // 테스트를 실행할 때 Mockito를 이용해 Mock을 만들것임을 명시
class MailServiceTest {

    @Spy
    private MailSendClient mailSendClient;
    @Mock
    private MailSendHistoryRepository mailSendHistoryRepository;
    @InjectMocks
    private MailService mailService;

    @Test
    @DisplayName("메일 전송 테스트")
    void sendMail() {
        // given
        // stubbing
//        Mockito.when(mailSendClient.sendEmail(anyString(), anyString(), anyString(), anyString()));
//        BDDMockito.given(mailSendClient.sendEmail(anyString(), anyString(), anyString(), anyString()))
//                .willReturn(true);
        doReturn(true)
                .when(mailSendClient)
                .sendEmail(anyString(), anyString(), anyString(), anyString());

        // when
        boolean result = mailService.sendMail("", "", "", "");

        // then
        Assertions.assertThat(result).isTrue();
        verify(mailSendHistoryRepository, times(1)).save(any(MailSendHistory.class));
    }

}