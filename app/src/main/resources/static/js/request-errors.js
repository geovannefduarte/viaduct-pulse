const requestErrorEvents = ["htmx:responseError", "htmx:sendError", "htmx:timeout"];

function showRequestError(event) {
  const source = event.detail.elt;
  const slot = source.querySelector("[data-request-error-slot]");
  if (!slot) {
    showPageError();
    return;
  }
  if (slot.querySelector(".request-error")) {
    return;
  }

  const busyRegion = source.querySelector("[aria-busy]");
  const loadingContent = Array.from(slot.childNodes);
  const error = document.getElementById("request-error").content.firstElementChild.cloneNode(true);
  error.querySelector("[data-request-retry]").addEventListener("click", () => {
    slot.replaceChildren(...loadingContent);
    busyRegion?.setAttribute("aria-busy", "true");
    htmx.trigger(source, "retry");
  });

  busyRegion?.setAttribute("aria-busy", "false");
  slot.replaceChildren(error);
}

function showPageError() {
  const pageErrors = document.getElementById("request-errors");
  pageErrors.textContent = pageErrors.dataset.message;
  pageErrors.hidden = false;
}

requestErrorEvents.forEach((name) => document.addEventListener(name, showRequestError));
