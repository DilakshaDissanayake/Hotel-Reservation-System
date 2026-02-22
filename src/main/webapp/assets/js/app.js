(function () {
    'use strict';

    if (!document.querySelector('.auth-scene')) return;

    document.querySelectorAll('.auth-eye').forEach(function (btn) {
        btn.addEventListener('click', function () {
            var targetId = btn.getAttribute('data-target');
            var input    = document.getElementById(targetId);
            var icon     = btn.querySelector('.bi');
            if (!input || !icon) return;

            var isHidden = input.type === 'password';
            input.type   = isHidden ? 'text' : 'password';

            icon.classList.toggle('bi-eye',        !isHidden);
            icon.classList.toggle('bi-eye-slash',   isHidden);

            btn.setAttribute('aria-label', isHidden ? 'Hide password' : 'Show password');
        });
    });

    document.querySelectorAll('.auth-check-input').forEach(function (cb) {
        cb.dispatchEvent(new Event('change'));
    });

    var form = document.getElementById('app-login-form');
    if (form) {
        form.addEventListener('submit', function (e) {
            var emailInput = form.querySelector('input[type="email"]');
            var pwInput    = form.querySelector('input[type="password"]');
            var valid      = true;

            [emailInput, pwInput].forEach(function (field) {
                if (!field) return;
                if (!field.value.trim()) {
                    field.classList.add('app-input--err');
                    valid = false;
                } else {
                    field.classList.remove('app-input--err');
                }
            });

            if (!valid) {
                e.preventDefault();
                var btn = form.querySelector('.auth-submit');
                if (btn) {
                    btn.style.animation = 'au-shake 0.35s ease';
                    btn.addEventListener('animationend', function () {
                        btn.style.animation = '';
                    }, { once: true });
                }
            }
        });

        form.querySelectorAll('.auth-input').forEach(function (input) {
            input.addEventListener('input', function () {
                input.classList.remove('auth-input--err');
            });
        });
    }

})();
