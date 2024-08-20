// 마커를 담을 배열
// let markers = [];

// 선택된 장소들을 저장할 배열
// let selectedPlaces = [];

const mapContainer = document.getElementById('map'), // 지도를 표시할 div
    mapOption = {
        center: new kakao.maps.LatLng(37.566826, 126.9786567), // 지도의 중심좌표
        level: 3 // 지도의 확대 레벨
    };

// 지도를 생성합니다
const map = new kakao.maps.Map(mapContainer, mapOption);

// 지도를 재설정할 범위정보를 가지고 있을 LatLngBounds 객체를 생성
const bounds = new kakao.maps.LatLngBounds();

// 장소 검색 객체를 생성합니다
const ps = new kakao.maps.services.Places();

// 키워드로 장소를 검색합니다
// searchPlaces();

// 선을 구성하는 좌표 배열입니다. 이 좌표들을 이어서 선을 표시합니다.
let linePath = {};

// 폴리라인(선) 객체 배열
let polyline = {};

// 마커 이미지가 순환되도록 매핑
const markerImages = {
    1: '/files/markers/marker_number_red.png',
    2: '/files/markers/marker_number_yellow.png',
    3: '/files/markers/marker_number_green.png',
    4: '/files/markers/marker_number_cyan.png',
    5: '/files/markers/marker_number_blue.png',
    6: '/files/markers/marker_number_purple.png'
}

/**
 * 마커 이미지 색상과 장소명 앞 span 태그의 글자 색상 연관 짓기
 * @param index
 * @returns {string}
 */
function getColorCode(index) {
    const colors = [
        '#F78181',  // red
        '#F7D358',  // yellow
        '#82FA58',  // green
        '#58FAF4',  // cyan
        '#58ACFA',  // blue
        '#9F81F7'   // purple
    ];
    return colors[index-1]; // colors는 0부터 시작하고 markerIndex는 1부터 시작하기 때문에 -1 해서 값 맞춰줌
}

/**
 * 키워드 검색을 요청하는 함수입니다
 * @returns {boolean} - 키워드가 유효하지 않으면 false
 */
function searchPlaces() {
    const keyword = document.getElementById('keyword').value;

    if (!keyword.replace(/^\s+|\s+$/g, '')) {   // 키워드가 공백만 있는 경우
        alert('키워드를 입력해주세요!');
        return false;
    }

    // 장소검색 객체를 통해 키워드로 장소검색을 요청합니다
    ps.keywordSearch(keyword, placesSearchCB);
}

/**
 * 장소검색이 완료됐을 때 호출되는 콜백함수입니다
 * @param data - 검색된 장소 데이터 배열
 * @param status - 검색 결과 응답 상태 코드
 * @param pagination - 페이지네이션 객체
 */
function placesSearchCB(data, status, pagination) {
    if (status === kakao.maps.services.Status.OK) {
        // 정상적으로 검색이 완료됐으면 검색 목록과 마커를 표출합니다
        displayPlaces(data);

        // 페이지 번호를 표출합니다
        displayPagination(pagination);
    } else if (status === kakao.maps.services.Status.ZERO_RESULT) {
        alert('검색 결과가 존재하지 않습니다.');
        return;
    } else if (status === kakao.maps.services.Status.ERROR) {

        alert('검색 결과 중 오류가 발생했습니다.');
        return;
    }
}

/**
 * 장소 데이터를 백단으로 전송하는 함수입니다
 * @param places - 선택된 장소 데이터 배열
 */
function sendPlaceData(places) {
    // 백단에서 필요한 데이터만 포함된 배열 생성
    const filteredPlaces = selectedPlaces.map(place => ({
        name: place.place_name,
        longitude: place.x,
        latitude: place.y
    }));

    $.ajax({
        url: '/api/save-location',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(filteredPlaces),
        success: function (data) {
            console.log('success:', data);
        },
        error: function (error) {
            console.error('error:', error);
        }
    });
}

/**
 * 선택 버튼 클릭 이벤트 핸들러
 * @param event - 이벤트 객체
 * @param place - 선택된 장소 데이터
 * @param placePosition - 장소의 좌표 객체
 */
function handleSelectBtnClick(event, place, placePosition) {
    event.stopPropagation();    // 이벤트 버블링 방지

    // addLocation에서 반환된 dayNum을 가져옴
    const dayNum = addTripLocation(selectedDateDiv);

    if (!dayNum) {
        alert('날짜를 선택해 주세요.');
        return;
    }

    const locationsContainer = selectedDateDiv.querySelector('.trip-locations');
    const MAX_LOCATIONS = 15;

    if (selectedPlaces[dayNum].length >= MAX_LOCATIONS) {
        alert(`하루에 추가할 수 있는 장소는 최대 ${MAX_LOCATIONS}개입니다.`);
        return;
    }

    // 장소의 고유 ID 생성 (ex. 몇일차-몇번째장소: 1-1, 1-2, 2-1, ..) 나중에 삭제 시 같은 장소 겹침 문제 해결 위해 고유 ID 지정
    const uniqueId = `${dayNum}-${selectedPlaces[dayNum].length + 1}`;
    const markerImageIndex = (dayNum % 6) === 0 ? 6 : (dayNum % 6);    // 마커 이미지 색상이랑 맞추기 (1 ~ 6 순환)
    const colorCode = getColorCode(markerImageIndex);

    const locations = document.createElement('div');
    locations.className = 'trip-location';
    locations.innerHTML = `
            <div class="placeNameContainer">
                <span id="${uniqueId}" style="color: ${colorCode};">${selectedPlaces[dayNum].length + 1}</span>
                <input type="text" class="placeNameInput" name="placeNameInput" value="${place.place_name}" readonly />
                <button type="button" onclick="handleRemoveBtnClick(event, '${uniqueId}', ${placePosition.La}, ${placePosition.Ma})">삭제</button>
            </div>
            <input type="hidden" class="latitudeInput" name="latitudeInput" value="${place.y}" />
            <input type="hidden" class="longitudeInput" name="longitudeInput" value="${place.x}" />
        `;
    locationsContainer.appendChild(locations);

    // 15개 초과가 아니라면 선택 장소 목록 배열에 추가
    selectedPlaces[dayNum].push(place);

    // 배열 인덱스로 마커에 번호 부여
    const index = selectedPlaces[dayNum].length;
    // 지도에 마커 추가
    addSelectedMarker(placePosition, index, dayNum);
    // 마커 번호 재정렬
    reorderMarkers(markers, dayNum);

    // 지도 범위 재설정
    setBounds(placePosition);

    // 지도에 선 표시
    drawLinePath(dayNum, placePosition);
}

/**
 * 장소 추가 시 지도에 선 그리기
 * @param dayNum - 여행 일차
 * @param placePosition - kakao.maps.LatLng 객체 (위도, 경도 위치 정보)
 */
function drawLinePath(dayNum, placePosition) {
    // linePath[dayNum]이 존재하지 않으면 빈 배열로 초기화
    if (!linePath[dayNum]) {
        linePath[dayNum] = [];
    }
    // 좌표 추가
    linePath[dayNum].push(placePosition);

    // 선 색상 지정
    const markerImageIndex = (dayNum % 6) === 0 ? 6 : (dayNum % 6);    // 마커 이미지 색상이랑 맞추기 (1 ~ 6 순환)
    const colorCode = getColorCode(markerImageIndex);

    // 이미 그려진 선이 있으면 지도에서 제거 (제거 안 하면 선이 겹쳐서 그려짐)
    if (polyline[dayNum]) {
        polyline[dayNum].setMap(null);
    }

    // 지도에 표시할 선을 생성합니다
    polyline[dayNum] = new kakao.maps.Polyline({
        path: linePath[dayNum], // 선을 구성하는 좌표배열 입니다
        strokeWeight: 4, // 선의 두께 입니다
        strokeColor: colorCode, // 선의 색깔입니다
        strokeOpacity: 0.7, // 선의 불투명도 입니다 1에서 0 사이의 값이며 0에 가까울수록 투명합니다
        strokeStyle: 'dash' // 선의 스타일입니다
    });

    // 지도에 선을 표시합니다
    polyline[dayNum].setMap(map);
}

/**
 * 장소 제거 시 지도에 선 알맞게 다시 그리기
 * @param dayNum
 */
function redrawLinePath(dayNum) {
    // 경로가 비어있으면 폴리라인 삭제
    if (linePath[dayNum].length === 0) {
        polyline[dayNum].setMap(null);
        delete polyline[dayNum];
    } else {
        polyline[dayNum].setMap(null);
        polyline[dayNum].setPath(linePath[dayNum]);

        // 지도에 선을 표시합니다
        polyline[dayNum].setMap(map);
    }
}

/**
 * 제거 버튼 클릭 이벤트 핸들러
 * @param event - 이벤트 객체
 * @param placeUniqueId - 선택된 요소의 장소 식별자
 * @param placePositionLa - 장소의 좌표 정보
 * @param placePositionMa - 장소의 좌표 정보
 */
function handleRemoveBtnClick(event, placeUniqueId, placePositionLa, placePositionMa) {
    event.stopPropagation();

    // 클릭된 버튼
    const button = event.target;

    // 버튼의 부모 요소
    const parentElement = button.parentElement;

    // 부모 요소 내에서 형제 요소 중 span 태그 찾기
    const spanElement = parentElement.querySelector('span');

    // span 태그의 id에서 dayNum과 placeIndex 추출
    const [dayNum, placeIndex] = spanElement.id.split('-').map(Number);
    // const [, placeIndex] = spanElement.id.split('-').map(Number);

    // selectedPlaces에서 해당 날짜와 인덱스에 있는 장소를 삭제
    if (selectedPlaces[dayNum]) {
        selectedPlaces[dayNum].splice(placeIndex - 1, 1); // 인덱스는 0부터 시작하므로 -1

        // placesContainer에서 해당 장소를 포함하는 element를 찾아서 삭제
        parentElement.parentElement.remove();

        // markers 배열에서 해당 장소 위치에 해당하는 marker 요소 제거
        const tolerance = 0.00000001; // 허용 오차 범위 설정

        // findIndex는 배열에 각 요소에 대해 조건을 만족하는 첫 번째 요소의 인덱스 반환
        const markerIndex = markers[dayNum].findIndex(marker => {
            const markerPos = marker.getPosition();
            // 오차 범위 이내면 같은 장소로 취급
            return Math.abs(markerPos.La - placePositionLa) < tolerance &&
                Math.abs(markerPos.Ma - placePositionMa) < tolerance;
        });

        if (markerIndex !== -1) {   // 해당 장소 위치의 마커가 존재한다면
            markers[dayNum][markerIndex].setMap(null);  // 지도에서 마커 제거
            markers[dayNum].splice(markerIndex, 1); // 배열에서 마커 제거. 인덱스부터 1개의 요소 삭제
        }

        // linePath에서 해당하는 위치 좌표 제거
        const pathIndex = linePath[dayNum].findIndex(position => {
            return Math.abs(position.La - placePositionLa) < tolerance &&
                Math.abs(position.Ma - placePositionMa) < tolerance;
        });

        if (pathIndex !== -1) {   // 해당 장소 위치 좌표가 존재한다면
            linePath[dayNum].splice(pathIndex, 1); // 배열에서 해당 좌표 제거. 인덱스부터 1개의 요소 삭제
        }

        console.log('handleRemoveBtnClick --- Selected Places:', selectedPlaces);
        console.log('handleRemoveBtnClick --- Markers:', markers);
        console.log('----------');

        // 장소와 마커가 삭제된 후, 나머지 요소들의 인덱스와 ID를 업데이트
        updatePlaceIndexes(dayNum);
        // 마커 번호 재정렬
        reorderMarkers(markers[dayNum], dayNum);
        // 장소 지웠으니 선 다시 그리기
        redrawLinePath(dayNum);

        // console.log('삭제 후 selectedPlaces[dayNum].length: ', selectedPlaces[dayNum].length);

        // } else {
        //     console.error('장소 인덱스가 유효하지 않습니다.');
        // }
    } else {
        console.error('dayNum이 존재하지 않습니다.');
    }
}

/**
 * 삭제 후 나머지 장소의 인덱스와 ID를 업데이트하는 함수
 * @param dayNum - 선택된 날짜의 번호
 */
function updatePlaceIndexes(dayNum) {
    // 해당 날짜의 모든 placeNameContainer 요소를 선택
    const locationElements = document.querySelectorAll(`.trip-date:nth-child(${dayNum}) .trip-location .placeNameContainer`);

    // 각 placeNameContainer의 span 태그의 id와 내용을 업데이트
    locationElements.forEach((placeNameContainer, index) => {
        const spanElement = placeNameContainer.querySelector('span');
        if (spanElement) {
            const newId = `${dayNum}-${index + 1}`;
            spanElement.id = newId; // 새로운 id 설정
            spanElement.textContent = index + 1; // 새로운 번호 설정
        }
    });
    console.log('updatePlaceIndexes --- Selected Places:', selectedPlaces);
    console.log('updatePlaceIndexes --- Markers:', markers);
    console.log('----------');

}

// 전체 날짜의 장소 인덱스를 재정렬하는 함수
function updateAllPlaceIndexes() {
    Object.keys(markers).forEach(dayNum => {
        updatePlaceIndexes(Number(dayNum));
    });
}

/**
 * 검색 결과 목록과 마커를 표출하는 함수입니다
 * @param places - 검색된 장소 데이터 배열
 */
function displayPlaces(places) {
    const listEl = document.getElementById('placesList'),
        menuEl = document.getElementById('menu_wrap'),
        fragment = document.createDocumentFragment(),
        listStr = '';

    // 검색 결과 목록에 추가된 항목들을 제거합니다
    removeAllChildNods(listEl);

    for (let i = 0; i < places.length; i++) {
        // 마커를 생성하고 지도에 표시합니다
        const itemEl = getListItem(i, places[i]); // 검색 결과 항목 Element를 생성합니다
        const placePosition = new kakao.maps.LatLng(places[i].y, places[i].x);  // 지도를 재설정할 범위정보를 가지고 있을 LatLngBounds 객체를 생성합니다

        // 항목 당 선택 버튼 클릭 이벤트
        const selectBtn = itemEl.querySelector('.select-btn');
        selectBtn.addEventListener('click', function (event) {
            handleSelectBtnClick(event, places[i], placePosition);
        });

        fragment.appendChild(itemEl);
    }

    // 검색결과 항목들을 검색결과 목록 Element에 추가합니다
    listEl.appendChild(fragment);
    menuEl.scrollTop = 0;
}

/**
 * 검색결과 항목을 Element로 반환하는 함수입니다
 * @param index - 검색 결과 항목의 인덱스
 * @param places - 검색 결과 항목의 데이터
 * @returns {HTMLLIElement} - 검색 결과 항목 Element
 */
function getListItem(index, places) {

    const el = document.createElement('li');
    // let itemStr = '<span class="markerbg marker_' + (index+1) + '"></span>' +
    let itemStr = '<div class="info">' +
        '   <h5>' + places.place_name + '</h5>';

    if (places.road_address_name) {
        itemStr += '    <span>' + places.road_address_name + '</span>' +
            '   <span class="jibun gray">' + places.address_name + '</span>';
    } else {
        itemStr += '    <span>' + places.address_name + '</span>';
    }

    itemStr += '  <span class="tel">' + places.phone + '</span>' +
        '</div>';

    itemStr += '<div><button class="select-btn" id="selectBtn">선택</button></div>';

    el.innerHTML = itemStr;
    el.className = 'item';

    return el;
}

/**
 * 선택된 장소의 마커를 생성하고 지도 위에 마커를 표시하는 함수입니다.
 * @param position - 마커의 좌표 객체
 * @param idx - 마커의 인덱스
 * @returns {kakao.maps.Marker} - 생성된 마커 객체
 */
function addSelectedMarker(position, idx, dayNum) {
    // dayNum % 6 === 0 이면 6으로 매핑(dayNum이 1 또는 7이면 1번째 이미지, 6이면 6번째 이미지, 2 또는 8이면 2번째 이미지, ... 6 단위로 순환)
    const imageIndex = (dayNum % 6) === 0 ? 6 : (dayNum % 6);
    // const imageSrc = 'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/marker_number_blue.png', // 마커 이미지 url, 스프라이트 이미지를 씁니다
    const imageSrc = markerImages[imageIndex],
        imageSize = new kakao.maps.Size(36, 37),  // 마커 이미지의 크기
        imgOptions = {
            spriteSize: new kakao.maps.Size(36, 691), // 스프라이트 이미지의 크기
            spriteOrigin: new kakao.maps.Point(0, ((idx - 1) * 46) + 10), // 스프라이트 이미지 중 사용할 영역의 좌상단 좌표
            offset: new kakao.maps.Point(13, 37) // 마커 좌표에 일치시킬 이미지 내에서의 좌표
        },
        markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize, imgOptions),
        marker = new kakao.maps.Marker({
            position: position, // 마커의 위치
            image: markerImage
        });

    marker.setMap(map);
    // markers.push(marker);
    markers[dayNum].push(marker);
    console.log('addSelectedMarker --- markres: {}', markers);
    console.log('----------');

    return marker;
}

/**
 * 이미 추가되어있던 장소의 마커를 지도에 표시하기
 * @param latitude
 * @param longitude
 * @param idx
 * @returns {kakao.maps.Marker}
 */
function addSavedPlacesMarker(latitude, longitude, idx, dayNum) {
    // dayNum % 6 === 0 이면 6으로 매핑(dayNum이 1 또는 7이면 1번째 이미지, 6이면 6번째 이미지, 2 또는 8이면 2번째 이미지, ... 6 단위로 순환)
    const imageIndex = (dayNum % 6) === 0 ? 6 : (dayNum % 6);
    // const imageSrc = 'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/marker_number_blue.png', // 마커 이미지 url, 스프라이트 이미지를 씁니다
    const imageSrc = markerImages[imageIndex],
        imageSize = new kakao.maps.Size(36, 37),  // 마커 이미지의 크기
        imgOptions = {
            spriteSize: new kakao.maps.Size(36, 691), // 스프라이트 이미지의 크기
            spriteOrigin: new kakao.maps.Point(0, ((idx) * 46) + 10), // 스프라이트 이미지 중 사용할 영역의 좌상단 좌표
            offset: new kakao.maps.Point(13, 37) // 마커 좌표에 일치시킬 이미지 내에서의 좌표
        },
        markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize, imgOptions),
        marker = new kakao.maps.Marker({
            position: new kakao.maps.LatLng(latitude, longitude), // 마커의 위치
            image: markerImage
        });

    marker.setMap(map);
    markers[dayNum].push(marker);
    setBounds(new kakao.maps.LatLng(latitude, longitude));
    console.log('addSavedPlacesMarker --- markers: {}', markers);
    console.log('----------');

    return marker;
}

/**
 * 제거 버튼 클릭 이벤트 발생 시 기존에 있던 마커 번호(마커 이미지)를 재정렬하는 함수
 * @param markers - 선택된 장소들의 마커 배열
 */
function reorderMarkers(markers, dayNum) {
    for (let i = 0; i < markers.length; i++) {
        const marker = markers[i];

        // dayNum % 6 === 0 이면 6으로 매핑(dayNum이 1 또는 7이면 1번째 이미지, 6이면 6번째 이미지, 2 또는 8이면 2번째 이미지, ... 6 단위로 순환)
        const imageIndex = (dayNum % 6) === 0 ? 6 : (dayNum % 6);
        // const imageSrc = 'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/marker_number_blue.png', // 마커 이미지 url, 스프라이트 이미지를 씁니다
        const imageSrc = markerImages[imageIndex],
            imageSize = new kakao.maps.Size(36, 37),  // 마커 이미지의 크기
            imgOptions = {
                spriteSize: new kakao.maps.Size(36, 691), // 스프라이트 이미지의 크기
                spriteOrigin: new kakao.maps.Point(0, (i * 46) + 10), // 스프라이트 이미지 중 사용할 영역의 좌상단 좌표
                offset: new kakao.maps.Point(13, 37) // 마커 좌표에 일치시킬 이미지 내에서의 좌표
            },
            markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize, imgOptions);

        marker.setImage(markerImage);
    }
}

/**
 * 지도 위에 표시되고 있는 마커를 모두 제거합니다
 */
function removeMarker() {
    for (let i = 0; i < markers.length; i++) {
        markers[i].setMap(null);
    }
    markers = [];
}

/**
 * 지도 범위 재설정 함수입니다
 * @param placePosition - 마커의 좌표 객체
 */
function setBounds(placePosition) {
    // LatLngBounds 객체에 좌표를 추가
    bounds.extend(placePosition);
    // LatLngBounds 객체에 추가된 좌표들을 기준으로 지도의 범위를 재설정합니다
    // 이때 지도의 중심좌표와 레벨이 변경될 수 있습니다
    map.setBounds(bounds);
}

/**
 * 검색결과 목록 하단에 페이지번호를 표시는 함수입니다
 * @param pagination - 페이지네이션 객체
 */
function displayPagination(pagination) {
    const paginationEl = document.getElementById('pagination');
    const fragment = document.createDocumentFragment();
    let i;

    // 기존에 추가된 페이지번호를 삭제합니다
    while (paginationEl.hasChildNodes()) {
        paginationEl.removeChild(paginationEl.lastChild);
    }

    for (i = 1; i <= pagination.last; i++) {
        const el = document.createElement('a');
        el.href = "#";
        el.innerHTML = i;

        if (i === pagination.current) {
            el.className = 'on';
        } else {
            el.onclick = (function (i) {
                return function () {
                    pagination.gotoPage(i);
                }
            })(i);
        }

        fragment.appendChild(el);
    }
    paginationEl.appendChild(fragment);
}

/**
 * 검색결과 목록의 자식 Element를 제거하는 함수입니다
 * @param el - 검색 결과 목록 요소
 */
function removeAllChildNods(el) {
    while (el.hasChildNodes()) {
        el.removeChild(el.lastChild);
    }
}

// 선택 완료 버튼에 클릭 이벤트 리스너 추가
// document.querySelector('button[type="button"]').addEventListener('click', handleSelectCompleteBtnClick);
