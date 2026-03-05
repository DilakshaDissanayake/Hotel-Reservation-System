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

function initTableSearch(inputId, tableId) {
    var input = document.getElementById(inputId);
    var table = document.getElementById(tableId);
    if (!input || !table) return;

    var tbody = table.querySelector('tbody');
    if (!tbody) return;

    var colCount = table.querySelectorAll('thead th').length || (tbody.rows[0] ? tbody.rows[0].cells.length : 1);
    var noResultsRow = tbody.querySelector('tr.search-no-results');
    if (!noResultsRow) {
        noResultsRow = document.createElement('tr');
        noResultsRow.className = 'search-no-results';
        noResultsRow.style.display = 'none';
        noResultsRow.innerHTML = '<td colspan="' + colCount + '" class="text-center py-4 text-muted">No matching results found.</td>';
        tbody.appendChild(noResultsRow);
    }

    function normalize(text) {
        return String(text || '').toLowerCase().replace(/\s+/g, ' ').trim();
    }

    function runSearch() {
        var term = normalize(input.value);
        var visibleCount = 0;

        for (var i = 0; i < tbody.rows.length; i++) {
            var row = tbody.rows[i];
            if (row.classList.contains('search-no-results')) continue;

            var text = normalize(row.textContent);
            var match = term === '' || text.indexOf(term) !== -1;
            row.style.display = match ? '' : 'none';
            if (match) visibleCount++;
        }

        noResultsRow.style.display = visibleCount === 0 ? '' : 'none';
    }

    input.addEventListener('input', runSearch);
    input.addEventListener('keyup', runSearch);
    runSearch();
}

function initPageTableSearches() {
    initTableSearch('reservationSearch', 'reservationTable');
    initTableSearch('roomSearch', 'roomTable');
}

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initPageTableSearches);
} else {
    initPageTableSearches();
}
