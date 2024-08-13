$(document).ready(function() {
    function validateForm() {
        let isValid = true;

        // 닉네임, 연락처 모두 입력되었는지 확인
        if (!$('#usernick').val() || !$('#phoneNumber').val()) {
            isValid = false;
        }

        // 닉네임 중복 여부 검사
        if ($('#usernickCheckMessage').hasClass('error')) {
            isValid = false;
        }
        $('#submit-button').prop('disabled', !isValid);

        return isValid;
    }

    // 폼 제출 이벤트 처리
    $('form').on('submit', function(event) {
        if (!validateForm()) {
            alert('모든 필드를 올바르게 입력해주세요.');
            event.preventDefault(); // 폼 제출 막기
        }
    });

    // 랜덤 닉네임 생성
    $("#generate-nickname-button").click(function() {
        $.ajax({
            url: '/api/users/randomNickname',
            type: 'GET',
            success: function(data) {
                $("#usernick").val(data);
                $("#usernickCheckMessage")
                    .text("랜덤 닉네임이 생성되었습니다. 중복 확인을 해주세요.")
                    .removeClass('error').addClass('success');
                validateForm();
            },
            error: function(xhr, status, error) {
                console.error("랜덤 닉네임 생성 중 오류 발생:", error);
                $("#usernickCheckMessage")
                    .text("랜덤 닉네임 생성에 실패했습니다.")
                    .removeClass('success').addClass('error');
            }
        });
    });

    // 닉네임 중복 확인
    $('#check-usernick-button').on('click', function() {
        let usernick = $('#usernick').val();

        $.ajax({
            type: 'POST',
            url: '/api/users/check-usernick',
            contentType: 'application/json',
            data: JSON.stringify({ usernick: usernick }),
            success: function(response) {
                if (response) {
                    $('#usernickCheckMessage').text('닉네임이 이미 존재합니다.').removeClass('success').addClass('error');
                } else {
                    $('#usernickCheckMessage').text('사용 가능한 닉네임입니다.').removeClass('error').addClass('success');
                }
                validateForm();
            },
            error: function(error) {
                $('#usernickCheckMessage').text('닉네임 확인 중 오류가 발생했습니다. 다시 시도해주세요.').removeClass('success').addClass('error');
                validateForm();
            }
        });
    });
});