$(document).ready(function() {
    function validateForm() {
        let isValid = true;

        // 닉네임 중복 여부 검사
        if ($('#usernickCheckMessage').hasClass('error')) {
            isValid = false;
        }
        $('#submit-button').prop('disabled', !isValid);

        return isValid;
    }

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

