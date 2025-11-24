import React from "react";

type InputProps = React.InputHTMLAttributes<HTMLInputElement> & {
  label?: string;
};

/**
 * A reusable input component with customizable styles.
 *
 * @param {string} [label] - The label of the input.
 * @param {string} [className] - Additional CSS classes to apply to the input.
 * @param {React.InputHTMLAttributes<HTMLInputElement>} rest - Additional props to pass to the input element.
 *
 * @example
 * <Input label="Name" className="mb-2" />
 */
export default function Input({ label, className = "", ...rest }: InputProps) {
  return (
    <div className="w-100">
      {label && (
        <label
          className="form-label fw-semibold mb-1"
          style={{ fontSize: "0.9rem" }}
        >
          {label}
        </label>
      )}

      <input
        {...rest}
        className={[
          "form-control",
          "rounded-3",
          "shadow-sm",
          "border-1",
          className,
        ]
          .filter(Boolean)
          .join(" ")}
        style={{ height: "42px" }}
      />
    </div>
  );
}
