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

    // selectedPlaces 배열의 각 요소 p의 id가 인자로 전달된 place의 id와 같은지 확인
    // .some()은 배열의 각 요소에 대한 테스트 -> true 또는 false 반환
    // const isAlreadySelected = selectedPlaces.some(p => p.id === place.id);

    // 이미 선택된 장소인지 확인
    // if (isAlreadySelected) {
    //     alert('이미 선택된 장소입니다.');
    //     return;
    // }

    // addLocation에서 반환된 dayNum을 가져옴
    const dayNum = addLocation(selectedDateDiv);

    if (!dayNum) {
        alert('날짜를 선택해 주세요.');
        return;
    }

    // 장소는 최대 15개까지만 선택되도록 제한
    if (selectedPlaces.length >= 15) {
        alert('최대 15개의 장소만 추가할 수 있습니다.');
        return;
    }

    const locationsContainer = selectedDateDiv.querySelector('.dayLocation');
    const MAX_LOCATIONS = 15;

    console.log('selectedPlaces[dayNum]: {}', selectedPlaces[dayNum]);
    if (selectedPlaces[dayNum].length >= MAX_LOCATIONS) {
        alert(`하루에 추가할 수 있는 장소는 최대 ${MAX_LOCATIONS}개입니다.`);
        return;
    }

    const locations = document.createElement('div');
    locations.className = 'locations';

    locations.innerHTML = `
            <div class="placeNameContainer">
                <span>${selectedPlaces[dayNum].length + 1}</span>
                <input type="text" class="placeNameInput" value="${place.place_name}" readonly />
                <button type="button" onclick="removeElement(this.parentNode)">삭제</button>
            </div>
            <input type="hidden" name="longitude" value="${place.x}" />
            <input type="hidden" name="latitude" value="${place.y}" />
        `;
    locationsContainer.appendChild(locations);

    // selectedPlaces.forEach(place => {
    //     const locations = document.createElement('div');
    //     locations.className = 'locations';
    //
    //     locations.innerHTML = `
    //     <input type="text" name="placeName" value="${place.place_name}" readonly />
    //     <input type="hidden" name="longitude" value="${place.x}" />
    //     <input type="hidden" name="latitude" value="${place.y}" />
    // `;
    //     locationsContainer.appendChild(locations);
    // });

    // selectedPlaces = [];
    // selectedDateDiv = null;
    // 선택 완료 버튼 누를 시 검색창 숨기기
    // $('#menu_wrap').hide();

    // 같은 id를 가진 장소가 없고, 15개 초과가 아니라면 선택 장소 목록 배열에 추가
    selectedPlaces[dayNum].push(place);

    // 배열 인덱스로 마커에 번호 부여
    const index = selectedPlaces[dayNum].length;
    // 지도에 마커 추가
    addSelectedMarker(placePosition, index, dayNum);
    // 마커 번호 재정렬
    reorderMarkers(markers);

    // 지도 범위 재설정
    setBounds(placePosition);
}

/**
 * 제거 버튼 클릭 이벤트 핸들러
 * @param event - 이벤트 객체
 * @param place - 선택된 장소 데이터
 * @param placePosition - 장소의 좌표 객체
 */
function handleRemoveBtnClick(event, place, placePosition) {
    event.stopPropagation();

    // 배열의 각 요소 p의 id가 인자로 전달된 place의 id와 다른 요소만 filter해서 selectedPlaces에 남김
    // (넘어온 place를 배열에서 삭제)
    selectedPlaces = selectedPlaces.filter(p => p.id !== place.id);

    // findIndex는 배열에 각 요소에 대해 조건을 만족하는 첫 번째 요소의 인덱스 반환
    // markers 배열에서 해당 장소 위치에 해당하는 marker 요소 제거
    const tolerance = 0.00000001; // 허용 오차 범위 설정

    const markerIndex = markers.findIndex(marker => {
        const markerPos = marker.getPosition();
        // 오차 범위 내면 같은 장소로 취급
        return Math.abs(markerPos.La - placePosition.La) < tolerance &&
            Math.abs(markerPos.Ma - placePosition.Ma) < tolerance;
    });

    if (markerIndex !== -1) {   // 해당 장소 위치의 마커가 존재한다면
        markers[markerIndex].setMap(null);  // 지도에서 마커 제거
        markers.splice(markerIndex, 1); // 배열에서 마커 제거. 인덱스부터 1개의 요소 삭제
        reorderMarkers(markers);    // 마커 번호 재정렬
    }

    // 지도 범위 재설정
    setBounds(placePosition);
}

/**
 * 선택 완료 버튼 클릭 이벤트 핸들러
 */
// function handleSelectCompleteBtnClick() {
//     if (selectedPlaces.length === 0) {
//         alert('선택된 장소가 없습니다. 장소를 추가해 주세요.');
//         return;
//     }
//     sendPlaceData(selectedPlaces);
//
//     // 선택된 장소를 createtrip.html에 전달하는 로직을 추가
//     window.selectedPlaces = [...selectedPlaces];
//
//     // 선택 완료 버튼 누를 시 검색창 숨기기
//     $('#menu_wrap').hide();
// }

// function handleSelectCompleteBtnClick() {
//     if (selectedPlaces.length === 0) {
//         alert('선택된 장소가 없습니다. 장소를 추가해 주세요.');
//         return;
//     }
//
//     if (!selectedDateDiv) {
//         alert('장소를 추가할 날짜를 선택해 주세요.');
//         return;
//     }
//
//     const locationsContainer = selectedDateDiv.querySelector('.dayLocation');
//     selectedPlaces.forEach(place => {
//         const locations = document.createElement('div');
//         locations.className = 'locations';
//
//         locations.innerHTML = `
//             <input type="text" name="placeName" value="${place.place_name}" readonly />
//             <input type="hidden" name="longitude" value="${place.x}" />
//             <input type="hidden" name="latitude" value="${place.y}" />
//         `;
//         locationsContainer.appendChild(locations);
//     });
//
//     selectedPlaces = [];
//     selectedDateDiv = null;
//     // 선택 완료 버튼 누를 시 검색창 숨기기
//     $('#menu_wrap').hide();
// }

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

        // 항목 당 제거 버튼 클릭 이벤트
        // const removeBtn = itemEl.querySelector('.remove-btn');
        // removeBtn.addEventListener('click', function (event) {
        //     handleRemoveBtnClick(event, places[i], placePosition);
        // });

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

    // itemStr += '<div><button class="select-btn" id="selectBtn">선택</button>' +
    //     '<button class="remove-btn" id="removeBtn">제거</button></div>';

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
    const imageSrc = 'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/marker_number_blue.png', // 마커 이미지 url, 스프라이트 이미지를 씁니다
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
    console.log('createtrip.js ----- ', markers);

    return marker;
}

/**
 * 제거 버튼 클릭 이벤트 발생 시 기존에 있던 마커 번호(마커 이미지)를 재정렬하는 함수
 * @param markers - 선택된 장소들의 마커 배열
 */
function reorderMarkers(markers) {
    for (let i = 0; i < markers.length; i++) {
        const marker = markers[i];

        const imageSrc = 'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/marker_number_blue.png', // 마커 이미지 url, 스프라이트 이미지를 씁니다
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
