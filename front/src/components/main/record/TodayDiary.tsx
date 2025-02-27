import SmilePhotoLg from "./SmilePhotoLg";
import SmilePhotoSm from "./SmilePhotoSm";
import DiaryThumbnail from "./DiaryThumbnail";
import { useNavigate } from "react-router-dom";

const TodayDiary = () => {
  const navigate = useNavigate();
  const handleDiaryDetail = () => {
    navigate("/record/diary");
  };

  return (
    <div className="w-[80%]">
      <div className="flex justify-between w-full text-sub-0 text-xs pb-[0.5rem]">
        <div>일지</div>
        <div onClick={handleDiaryDetail}>+더보기</div>
      </div>
      <div className="w-full flex justify-center">
        <div className="flex justify-center gap-3 w-full aspect-[6/4]">
          <div className="flex justify-center items-start h-full aspect-square">
            <SmilePhotoLg />
          </div>
          <div className="flex flex-col justify-start items-center h-full aspect-[1/2] gap-[0.5rem]">
            <SmilePhotoSm />
            <SmilePhotoSm />
          </div>
        </div>
      </div>
      <div
        onClick={handleDiaryDetail}
        className="w-full flex justify-center pt-[1rem] "
      >
        <DiaryThumbnail
          title="3월 05일에 있었던 우리 아가의 첫 웃음은 하루종일 생각나"
          content="날이 좋아서 그런지 아기가 빨리 성장해서 같이 나들이 가고싶어"
        />
      </div>
    </div>
  );
};

export default TodayDiary;
