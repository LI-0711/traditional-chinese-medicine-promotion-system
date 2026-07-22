(function () {
    "use strict";

    const username = localStorage.getItem("username");
    const welcome = document.getElementById("welcomeUser");
    if (!username || !welcome) return;

    const API_BASE = "";

    function addStyles() {
        if (document.getElementById("globalAvatarStyles")) return;
        const style = document.createElement("style");
        style.id = "globalAvatarStyles";
        style.textContent = `
            .global-user-avatar-group{display:flex;align-items:center;gap:12px;min-width:0}
            .global-avatar-wrap{width:44px;height:44px;border-radius:50%;background:#ffffff24;display:flex;align-items:center;justify-content:center;overflow:hidden;flex:0 0 44px}
            .global-avatar-image{width:100%;height:100%;object-fit:cover;display:none}
            .global-avatar-fallback{font-size:25px;line-height:1}
        `;
        document.head.appendChild(style);
    }

    function ensureElements() {
        const existingImage = document.getElementById("headerAvatar");
        const existingFallback = document.getElementById("headerAvatarFallback");
        if (existingImage && existingFallback) {
            return {image: existingImage, fallback: existingFallback};
        }

        addStyles();
        const parent = welcome.parentElement;
        const group = document.createElement("span");
        group.className = "global-user-avatar-group";
        const wrap = document.createElement("span");
        wrap.className = "global-avatar-wrap";
        const fallback = document.createElement("span");
        fallback.id = "headerAvatarFallback";
        fallback.className = "global-avatar-fallback";
        fallback.textContent = "👤";
        const image = document.createElement("img");
        image.id = "headerAvatar";
        image.className = "global-avatar-image";
        image.alt = "";
        wrap.append(fallback, image);
        parent.insertBefore(group, welcome);
        group.append(wrap, welcome);
        return {image, fallback};
    }

    const elements = ensureElements();

    function cleanWelcomeIcon() {
        for (const node of welcome.childNodes) {
            if (node.nodeType === Node.TEXT_NODE) {
                const cleaned = node.textContent.replace(/^\s*[👤👋]\s*/u, "");
                if (cleaned !== node.textContent) node.textContent = cleaned;
                break;
            }
        }
    }

    function showDefault() {
        elements.image.style.display = "none";
        elements.image.removeAttribute("src");
        elements.fallback.style.display = "inline";
    }

    function showImage(url) {
        elements.image.onload = function () {
            elements.image.style.display = "block";
            elements.fallback.style.display = "none";
        };
        elements.image.onerror = showDefault;
        elements.image.src = url;
    }

    async function refresh() {
        try {
            const response = await fetch(`${API_BASE}/user/profile?username=${encodeURIComponent(username)}`);
            if (!response.ok) throw new Error(response.status);
            const profile = await response.json();
            if (profile.avatarUrl) {
                showImage(API_BASE + profile.avatarUrl + "&v=" + Date.now());
            } else {
                showDefault();
            }
        } catch (error) {
            console.warn("Avatar unavailable", error);
            showDefault();
        }
    }

    const observer = new MutationObserver(cleanWelcomeIcon);
    observer.observe(welcome, {childList: true, characterData: true, subtree: true});
    cleanWelcomeIcon();
    refresh();
    window.TCMAvatar = {refresh};
})();
