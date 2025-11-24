/**
 * Retrieves the user object from local storage.
 *
 * If the user object is not found in local storage, returns null.
 *
 * @returns {Object|null} The user object or null if not found.
 */
export function getUser() {
  return JSON.parse(localStorage.getItem("user") || "null");
}
