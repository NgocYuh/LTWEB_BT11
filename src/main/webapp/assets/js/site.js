document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('img[data-fallback]').forEach(image => {
        image.addEventListener('error', () => {
            const fallback = image.dataset.fallback;
            if (image.getAttribute('src') !== fallback) image.src = fallback;
        });
        if (image.complete && image.naturalWidth === 0) image.src = image.dataset.fallback;
    });
});
