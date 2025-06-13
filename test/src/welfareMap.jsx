import React, { useEffect, useRef, useState } from "react";
import "ol/ol.css";
import Map from "ol/Map";
import View from "ol/View";
import TileLayer from "ol/layer/Tile";
import XYZ from "ol/source/XYZ";
import VectorLayer from "ol/layer/Vector";
import VectorSource from "ol/source/Vector";
import GeoJSON from "ol/format/GeoJSON";
import { toLonLat } from "ol/proj";
import { Style, Stroke, Fill } from "ol/style";
import WelfareCompareView from "./components/WelfareCompareView";
import WelfareBenefitView from "./components/WelfareBenefitView";

// ✅ 특례시 변환 매핑 (Reverse Geocoding 결과 → GeoJSON FULL_NM_CLEAN 대응용)
const specialCityNames = {
  수원시: "수원특례시",
  용인시: "용인특례시",
  성남시: "성남특례시",
  고양시: "고양특례시",
  창원시: "창원특례시",
  안양시: "안양시",
  안산시: "안산시",
};

const WelfareMap = () => {
  const mapElement = useRef(); // 지도 DOM 참조
  const mapRef = useRef(); // Map 객체 참조
  const selectedLayersRef = useRef([]); // 현재 표시 중인 Polygon Layer들
  const [selectedDistricts, setSelectedDistricts] = useState([]); // 선택된 지자체명 리스트 (렌더링용)
  const selectedDistrictsRef = useRef([]); // 중복 방지용 ref → 항상 최신 selectedDistricts 보유
  const [benefitsData, setBenefitsData] = useState([]); // 복지 혜택 데이터

  // ✅ 복지 혜택 데이터 로드 (처음 1회 실행)
  useEffect(() => {
    fetch("/benefitsData.json")
      .then((res) => res.json())
      .then((data) => {
        setBenefitsData(data);
      })
      .catch((err) => {
        console.error("복지혜택 데이터 로드 실패:", err);
      });
  }, []);

  // ✅ 지도 초기화 + 클릭 이벤트 등록 (처음 1회 실행)
  useEffect(() => {
    const map = new Map({
      target: mapElement.current,
      layers: [
        new TileLayer({
          source: new XYZ({
            url: "https://api.vworld.kr/req/wmts/1.0.0/6D96057A-FC81-30BE-B98E-39266237CBD1/Base/{z}/{y}/{x}.png",
            crossOrigin: "anonymous",
          }),
        }),
      ],
      view: new View({
        center: [14135362.197, 4518290.522], // 중심 좌표 (EPSG:3857)
        zoom: 7,
      }),
    });

    mapRef.current = map;

    map.on("click", (evt) => {
      const coordinate = evt.coordinate;
      const lonLat = toLonLat(coordinate); // EPSG:4326 변환

      console.log("지도 클릭 좌표 (경도, 위도):", lonLat[0], lonLat[1]);

      reverseGeocode(lonLat[0], lonLat[1]);
    });

    return () => map.setTarget(null); // 언마운트 시 cleanup
  }, []);

  // ✅ selectedDistricts → selectedDistrictsRef 에 항상 최신 복사
  useEffect(() => {
    selectedDistrictsRef.current = selectedDistricts;
  }, [selectedDistricts]);

  // ✅ Reverse Geocoding 호출
  const reverseGeocode = (lon, lat) => {
    fetch(`/api/welfare-curl/reverse-geocode?lon=${lon}&lat=${lat}`)
      .then((response) => response.json())
      .then((data) => {
        console.log("Reverse Geocoding 응답:", data);

        if (
          data.response &&
          data.response.result &&
          data.response.result.length > 0
        ) {
          const structure = data.response.result[0].structure;
          const fullName = `${structure.level1} ${structure.level2}`;
          console.log("🎯 클릭한 위치 지자체명:", fullName);

          // CLEAN NAME 변환
          const cleanFullName = mapCleanFullName(fullName);
          console.log("🔎 변환된 비교용 FULL_NM_CLEAN:", cleanFullName);

          // ✅ 중복 클릭 방지 (ref 기준)
          if (selectedDistrictsRef.current.includes(cleanFullName)) {
            console.log(
              `⚠️ 이미 선택된 지자체: ${cleanFullName} (중복 클릭 무시)`
            );
            return;
          }

          displayPolygon(cleanFullName);
        } else {
          console.warn("No address found.");
        }
      })
      .catch((error) => {
        console.error("Reverse Geocoding 실패:", error);
      });
  };

  // ✅ 특례시 변환 적용 (FULL_NM_CLEAN 대응용)
  const mapCleanFullName = (fullName) => {
    const tokens = fullName.split(" ");
    if (tokens.length < 2) return fullName;

    const sido = tokens[0];
    const city = tokens[1];

    if (specialCityNames[city]) {
      return `${sido} ${specialCityNames[city]}`;
    }

    return fullName;
  };

  // ✅ Polygon 표시
  const displayPolygon = (fullNameClean) => {
    fetch("/TL_SCCO_SIG_KDJ.json")
      .then((response) => response.json())
      .then((geojsonData) => {
        const matchedFeatures = geojsonData.features.filter((feature) => {
          const props = feature.properties;
          return (
            props.FULL_NM_CLEAN &&
            props.FULL_NM_CLEAN.trim() === fullNameClean.trim()
          );
        });

        console.log(`✅ 매칭된 feature 개수: ${matchedFeatures.length}`);

        if (matchedFeatures.length === 0) {
          console.warn(`❌ ${fullNameClean}에 해당하는 Polygon 없음`);
          return;
        }

        // ✅ 2개 선택 시 가장 먼저 선택한 것 제거
        if (selectedLayersRef.current.length >= 2) {
          const oldLayer = selectedLayersRef.current.shift();
          mapRef.current.removeLayer(oldLayer);

          setSelectedDistricts((prev) => {
            const newDistricts = prev.slice(1);

            // 나머지 Layer 색상 재적용
            selectedLayersRef.current.forEach((layer, idx) => {
              const colors = [
                { fill: "rgba(255, 0, 0, 0.4)", stroke: "#ff0000" },
                { fill: "rgba(0, 128, 255, 0.4)", stroke: "#007bff" },
              ];
              const color = colors[idx % 2];
              layer.setStyle(
                new Style({
                  stroke: new Stroke({
                    color: color.stroke,
                    width: 3,
                  }),
                  fill: new Fill({
                    color: color.fill,
                  }),
                })
              );
            });

            console.log("♻️ 첫번째 Polygon 제거 완료 & 색상 재적용");

            return newDistricts;
          });
        }

        // ✅ 현재 layer 색상 적용
        const index = selectedLayersRef.current.length;
        const colors = [
          { fill: "rgba(255, 0, 0, 0.4)", stroke: "#ff0000" },
          { fill: "rgba(0, 128, 255, 0.4)", stroke: "#007bff" },
        ];
        const color = colors[index % 2];

        const vectorSource = new VectorSource({
          features: new GeoJSON().readFeatures(
            {
              type: "FeatureCollection",
              features: matchedFeatures,
            },
            {
              featureProjection: "EPSG:3857",
            }
          ),
        });

        const vectorLayer = new VectorLayer({
          source: vectorSource,
          style: new Style({
            stroke: new Stroke({
              color: color.stroke,
              width: 3,
            }),
            fill: new Fill({
              color: color.fill,
            }),
          }),
        });

        mapRef.current.addLayer(vectorLayer);
        selectedLayersRef.current.push(vectorLayer);

        setSelectedDistricts((prev) => [...prev, fullNameClean]);

        console.log(
          `✅ Polygon 표시 완료 [현재 선택 수: ${selectedLayersRef.current.length}]`
        );
      })
      .catch((error) => {
        console.error("GeoJSON 로드 실패:", error);
      });
  };

  // ✅ 렌더링 영역
  return (
    <div>
      <h2>복지 지도</h2>
      <div
        ref={mapElement}
        style={{ width: "100%", height: "600px", marginTop: "10px" }}
      ></div>

      <div style={{ marginTop: "10px" }}>
        <h3>선택한 지자체:</h3>
        <ul>
          {selectedDistricts.map((district, index) => (
            <li key={index}>{district}</li>
          ))}
        </ul>
      </div>

      {/* ✅ 1개 선택 시 복지혜택 View */}
      {selectedDistricts.length === 1 && (
        <WelfareBenefitView
          district={selectedDistricts[0]}
          benefits={benefitsData}
        />
      )}

      {/* ✅ 2개 선택 시 복지혜택 비교 View */}
      {selectedDistricts.length === 2 && (
        <WelfareCompareView
          districtA={selectedDistricts[0]}
          districtB={selectedDistricts[1]}
          benefits={benefitsData}
        />
      )}
    </div>
  );
};

export default WelfareMap;
