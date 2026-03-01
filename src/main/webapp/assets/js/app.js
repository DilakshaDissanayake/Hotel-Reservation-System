(function () {
    'use strict';

    if (!document.querySelector('.app-scene')) return;

    // Ensure waves and sun animations are initialized
    document.addEventListener('DOMContentLoaded', function () {
        const waves = document.querySelectorAll('.app-wave');
        const sun = document.querySelector('.app-sun');

        if (waves.length) {
            waves.forEach((wave, index) => {
                wave.style.animationDelay = `${index * 2}s`;
            });
        }

        if (sun) {
            sun.style.animation = 'au-sunpulse 5s ease-in-out infinite';
        }
    });
})();

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
        var usernameInput = form.querySelector('input[name="username"]');
        var pwInput       = form.querySelector('input[name="password"]');
        var valid         = true;

        [usernameInput, pwInput].forEach(function (field) {
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
            var btn = form.querySelector('.app-submit');
            if (btn) {
                btn.style.animation = 'au-shake 0.35s ease';
                btn.addEventListener('animationend', function () {
                    btn.style.animation = '';
                }, { once: true });
            }
        }
    });

    form.querySelectorAll('.app-input').forEach(function (input) {
        input.addEventListener('input', function () {
            input.classList.remove('app-input--err');
        });
    });
}
