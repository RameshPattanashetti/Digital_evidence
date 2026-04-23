window.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('input, select').forEach((element) => {
        element.addEventListener('focus', () => {
            element.closest('form')?.classList.add('active');
        });

        element.addEventListener('blur', () => {
            element.closest('form')?.classList.remove('active');
        });
    });
});
