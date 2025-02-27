package ssafy.icon.alarmservice.domain.alarm.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import ssafy.icon.alarmservice.domain.alarm.client.dto.GetGuardianApiRes;
import ssafy.icon.alarmservice.domain.alarm.client.dto.GetMemberApiRes;

@FeignClient(name = "common-service")
public interface AlarmApiClient {

	@GetMapping("/members")
	GetMemberApiRes getMember(
		@RequestHeader("X-Authorization-Id") Integer memberId
	);

	@GetMapping("/members/guardian/token")
	List<GetGuardianApiRes> getGuardian(
		@RequestHeader("X-Authorization-Id") Integer memberId
	);
}
