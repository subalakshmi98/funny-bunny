/**
 * A reusable loading indicator component with customizable text.
 *
 * @param {string} [text="Loading..."] - The text to display while loading.
 *
 * @example
 * <Loading text="Please wait..." />
 */
export default function Loading({ text = "Loading..." }: { text?: string }) {
  return (
    <div className="d-flex flex-column justify-content-center align-items-center py-4">
      <div
        className="spinner-border text-primary mb-2"
        style={{ width: "2.5rem", height: "2.5rem" }}
        role="status"
      />
      <div className="text-muted fw-semibold">{text}</div>
    </div>
  );
}
