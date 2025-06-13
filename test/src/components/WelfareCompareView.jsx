import React from "react";
import WelfareBenefitView from "./WelfareBenefitView"; // 꼭 필요! import 해주세요

const WelfareCompareView = ({ districtA, districtB, benefits }) => {
  const benefitsA = benefits.filter((item) => item.FULL_NM === districtA);
  const benefitsB = benefits.filter((item) => item.FULL_NM === districtB);

  return (
    <div style={{ display: "flex", gap: "20px" }}>
      <div style={{ flex: 1 }}>
        <WelfareBenefitView district={districtA} benefits={benefits} />
      </div>
      <div style={{ flex: 1 }}>
        <WelfareBenefitView district={districtB} benefits={benefits} />
      </div>
    </div>
  );
};

export default WelfareCompareView;
