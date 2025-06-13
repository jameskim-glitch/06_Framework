import React from "react";

const WelfareBenefitView = ({ district, benefits }) => {
  return (
    <div>
      <h3>{district} 복지혜택</h3>
      {benefits.length === 0 ? (
        <p>등록된 복지 혜택이 없습니다.</p>
      ) : (
        <ul>
          {benefits.map((benefit, idx) => (
            <li key={idx} style={{ marginBottom: "10px" }}>
              <strong>{benefit.혜택명}</strong> ({benefit.카테고리}) <br />
              상세내용: {benefit.상세내용} <br />
              대상조건: {benefit.대상조건}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default WelfareBenefitView;
