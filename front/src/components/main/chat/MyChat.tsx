import React from "react";

const MyChat: React.FC<{ content: string; timestamp: Date }> = ({
  content,
  timestamp,
}) => {
  const formatTime = (date: Date): string => {
    const hours = date.getHours();
    const minutes = date.getMinutes();
    const isPM = hours >= 12;
    const formattedHours = isPM ? hours - 12 : hours;
    const formattedMinutes = minutes < 10 ? `0${minutes}` : minutes;
    return `${isPM ? "오후" : "오전"} ${formattedHours}:${formattedMinutes}`;
  };
  return (
    <div className="flex justify-end mb-[1rem]">
      <div className="text-black text-[0.6rem] mt-1 self-end mr-2">
        {formatTime(timestamp)}
      </div>
      <div className="max-w-[11rem] bg-blue-400 rounded-tl-[1rem] rounded-bl-[1rem] rounded-br-[1rem] p-3 flex items-center">
        <div className="text-white text-[0.8rem] font-normal break-words overflow-hidden">
          {content}
        </div>
      </div>
    </div>
  );
};

export default MyChat;
