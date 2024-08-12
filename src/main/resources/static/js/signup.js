$(document).ready(function() {
    function validateForm() {
        let isValid = true;

        // 이메일, 닉네임 중복 및 비밀번호 일치 여부 검사
        if ($('#emailCheckMessage').hasClass('error')) {
            isValid = false;
        }
        if ($('#usernickCheckMessage').hasClass('error')) {
            isValid = false;
        }
        if ($('#passwordMessage').hasClass('error')) {
            isValid = false;
        }
        $('#signup-button').prop('disabled', !isValid);

        return isValid;
    }

    // 이메일 중복 검사
    // 1. 이메일 중복되면 인증 메일 보내기 버튼은 숨김
    // 2. 이메일 중복이 없다면 인증 메일 보내기 버튼 활성화 됨
    $('#check-email-button').on('click', function() {
        let email = $('#email').val();

        $.ajax({
            type: 'POST',
            url: '/api/users/check-email',
            contentType: 'application/json',
            data: JSON.stringify({ mail: email }),
            success: function(response) {
                if (response === "아이디가 이미 존재합니다.") {
                    $('#emailCheckMessage').text(response).removeClass('success').addClass('error');
                    $('#send-code-button').hide();
                } else {
                    $('#emailCheckMessage').text(response).removeClass('error').addClass('success');
                    $('#send-code-button').show();
                }
                validateForm();
            },
            error: function(error) {
                $('#emailCheckMessage').text('이메일 확인 중 오류가 발생했습니다. 다시 시도해주세요.').removeClass('success').addClass('error');
                $('#send-code-button').hide();  // 오류가 있으면 버튼 숨김
                validateForm();
            }
        });
    });

    // 인증 메일
    $('#send-code-button').on('click', function() {
        let email = $('#email').val();

        $.ajax({
            type: 'POST',
            url: '/api/users/mail',
            contentType: 'application/json',
            data: JSON.stringify({ mail: email }),
            success: function(response) {
                $('#verifyCodeSection').show(); // 인증 코드 입력 섹션 표시
                alert('인증 메일이 발송되었습니다. 인증 번호를 확인해주세요.');
            },
            error: function(error) {
                alert('메일 발송에 실패했습니다. 다시 시도해주세요.');
            }
        });
    });

    // 인증 코드 확인
    $('#verify-code-button').on('click', function() {
        let email = $('#email').val();
        let code = $('#verificationCode').val();

        $.ajax({
            type: 'POST',
            url: '/api/users/verify-code',
            contentType: 'application/json',
            data: JSON.stringify({ mail: email, code: code }),
            success: function(response) {
                if (response === 'Verified') {
                    $('#verificationMessage').text('인증 성공').removeClass('error').addClass('success');
                } else {
                    $('#verificationMessage').text('인증 실패. 올바른 코드를 입력하세요.').removeClass('success').addClass('error');
                }
            },
            error: function(error) {
                $('#verificationMessage').text('인증 실패. 다시 시도해주세요.').removeClass('success').addClass('error');
            }
        });
    });

    // 랜덤 닉네임 생성
    $("#generate-nickname-button").click(function() {
        $.ajax({
            url: '/api/users/randomNickname',
            type: 'GET',
            success: function(data) {
                $("#usernick").val(data);
                $("#usernickCheckMessage").text("랜덤 닉네임이 생성되었습니다. 중복 확인을 해주세요.");
            },
            error: function(error) {
                console.error("랜덤 닉네임 생성 중 오류 발생:", error);
                $("#usernickCheckMessage").text("랜덤 닉네임 생성에 실패했습니다.");
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

    // 비밀번호 일치 확인
    $('#passwordCheck').on('input', function() {
        let password = $('#password').val();
        let passwordCheck = $('#passwordCheck').val();

        if (password !== passwordCheck) {
            $('#passwordMessage').text('비밀번호가 일치하지 않습니다.').addClass('error');
        } else {
            $('#passwordMessage').text('').removeClass('error');
        }
        validateForm();
    });

    $('#password').on('input', function() {
        $('#passwordCheck').trigger('input');
    });

    // 인증 코드 발송 버튼 --> 초기 상태에서는 비활성화
    $('#send-code-button').hide();

    // 인증 코드 입력 란 숨기기
    $('#verifyCodeSection').hide();
});
