package ssafy.icon.alarmservice.domain.alarm.service;

import static org.springframework.http.HttpStatus.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ssafy.icon.alarmservice.domain.alarm.client.AlarmApiClient;
import ssafy.icon.alarmservice.domain.alarm.client.dto.GetGuardianApiRes;
import ssafy.icon.alarmservice.domain.alarm.client.dto.GetMemberApiRes;
import ssafy.icon.alarmservice.domain.alarm.client.dto.KafkaProducerDto;
import ssafy.icon.alarmservice.global.error.exception.AlarmException;
import ssafy.icon.alarmservice.global.kafka.KafkaBabyCryProducer;

@RequiredArgsConstructor
@Service
@Slf4j
public class AlarmService {

	private final AlarmApiClient alarmApiClient;
	private final KafkaBabyCryProducer kafkaBabyCryProducer;

	// 울음 종류 : CRY, TIRED, HUNGRY, DISCOMFORT, DANGER, PAIN
	public boolean sendCryMessage(Integer memberId, String type) {
		GetMemberApiRes member = alarmApiClient.getMember(memberId);
		log.info("GetMemberApiRes : {}", member.toString());
		if (member.getWebToken().isEmpty()) {
			throw new AlarmException(BAD_REQUEST, "회원의 fcm 토큰이 존재하지 않습니다.");
		}

		// 보호자 토큰 조회
		List<GetGuardianApiRes> guardians = alarmApiClient.getGuardian(memberId);
		List<String> tokens = new ArrayList<>();
		tokens.add(member.getWebToken());

		for (GetGuardianApiRes guardian : guardians) {
			log.info("guardian : {}, {}", guardian.getGuestId(), guardian.getToken());
			if (guardian.getToken() != null)
				tokens.add(guardian.getToken());
		}

		boolean isAppToken = false;
		if(member.getAppToken() != null && !type.equals("CRY")){
			tokens.add(member.getAppToken());
			isAppToken = true;
		}

		log.info("tokens : {}", tokens.size());

		String titleMsg = "울음 분석 완료!";
		String bodyMsg = "";
		switch (type) {
			case "TIRED":
				bodyMsg = "아기가 졸려요!";
				break;
			case "HUNGRY":
				bodyMsg = "아기가 배고파요!";
				break;
			case "DISCOMFORT":
				bodyMsg = "아기가 불편해요!";
				break;
			case "PAIN":
				bodyMsg = "아기가 아파요!";
				break;
			case "DANGER":
				titleMsg = "아기 자세 감지!!";
				bodyMsg = "아기가 엎드려있어요!";
				break;
			default:
				titleMsg = "아기 울음 감지!!";
				bodyMsg = "아기가 울고있어요";
				break;
		}

		Notification noti = Notification.builder()
			.setTitle(titleMsg)
			.setBody(bodyMsg)
			.build();

		MulticastMessage msg = MulticastMessage.builder()
			.setNotification(noti)
			.addAllTokens(tokens)
			.build();

		try {
			BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(msg);
			log.info("Notification sent successfully: {}", response.getSuccessCount());
			if(isAppToken){
				return response.getResponses().getLast().isSuccessful();
			}
		} catch (FirebaseMessagingException e) {
			log.error("Failed to send notification", e);
			throw new RuntimeException(e);
		}
		return true;
	}

	public void addKafkaBabyCry(Integer memberId, String type) {
		log.info("Service add kafka baby-cry memberId : {}, {}", memberId, type);
		kafkaBabyCryProducer.send("baby-cry", new KafkaProducerDto(memberId, type));
	}
}
