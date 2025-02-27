package ssafy.icon.commonservice.domain.diary.service;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssafy.icon.commonservice.domain.diary.dto.DiaryDetailResponse;
import ssafy.icon.commonservice.domain.diary.dto.DiaryModifyForm;
import ssafy.icon.commonservice.domain.diary.dto.DiaryRegisterForm;
import ssafy.icon.commonservice.domain.diary.entity.Diary;
import ssafy.icon.commonservice.domain.diary.entity.DiaryImage;
import ssafy.icon.commonservice.domain.diary.repository.DiaryRepository;
import ssafy.icon.commonservice.domain.member.entity.Member;
import ssafy.icon.commonservice.domain.member.repository.MemberRepository;
import ssafy.icon.commonservice.global.error.exception.DiaryException;

@RequiredArgsConstructor
@Service
public class DiaryService {

	private final DiaryRepository diaryRepository;
	private final MemberRepository memberRepository;

	@Transactional
	public void post(Integer memberId, DiaryRegisterForm diaryRegisterForm) {
		if (diaryRepository.existsByMemberIdAndDate(memberId, diaryRegisterForm.date())) {
			throw new DiaryException(BAD_REQUEST, "이미 일지를 작성한 날짜입니다.");
		}

		Member member = memberRepository.getReferenceById(memberId);

		List<DiaryImage> images = diaryRegisterForm.imageUrls().stream()
			.map(DiaryImage::new).toList();

		Diary diary = Diary.builder()
			.member(member)
			.content(diaryRegisterForm.content())
			.date(diaryRegisterForm.date())
			.emoji(diaryRegisterForm.emoji())
			.build();

		images.forEach(diary::addImage);

		diaryRepository.save(diary);
	}

	@Transactional
	public void delete(Integer memberId, LocalDate date) {
		Diary diary = diaryRepository.findByMemberIdAndDate(memberId,date)
			.orElseThrow(() -> new DiaryException(NOT_FOUND, "성장일지를 찾을 수 없습니다."));

		diaryRepository.delete(diary);
	}

	@Transactional
	public void modify(Integer memberId, LocalDate date, DiaryModifyForm form) {
		Diary diary = diaryRepository.findByMemberIdAndDate(memberId, date)
			.orElseThrow(() -> new DiaryException(NOT_FOUND, "성장일지를 찾을 수 없습니다."));

		if (diaryRepository.existsByMemberIdAndDate(memberId, form.getDate())) {
			throw new DiaryException(BAD_REQUEST, "해당 날짜에 이미 작성한 일지가 있습니다.");
		}

		ArrayList<DiaryImage> deletedImages = new ArrayList<>();

		for (DiaryImage image : diary.getImages()) {
			boolean isExisted = false;
			for (String url : form.getImageUrls()) {
				if (image.getUrl().equals(url)) {
					isExisted = true;
					form.getImageUrls().remove(url);
					break;
				}
			}

			if (!isExisted) {
				deletedImages.add(image);
			}
		};

		diary.getImages().removeAll(deletedImages);

		for (String imageUrl : form.getImageUrls()) {
			diary.addImage(new DiaryImage(imageUrl));
		}

		diary.modify(form.getContent(), form.getDate(), form.getEmoji());
	}

	public DiaryDetailResponse queryDetail(Integer memberId, LocalDate date) {
		Diary diary = diaryRepository.findByMemberIdAndDate(memberId, date)
			.orElse(Diary.builder().build());

		return DiaryDetailResponse.of(diary);
	}

	public List<DiaryDetailResponse> queryPeriod(Integer memberId, LocalDate start, LocalDate end) {
		return diaryRepository.findAllByPeriod(memberId, start, end).stream()
			.map(DiaryDetailResponse::of).toList();
	}


}
