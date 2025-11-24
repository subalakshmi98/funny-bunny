import React from "react";

type ButtonProps = React.ButtonHTMLAttributes<HTMLButtonElement> & {
  variant?:
    | "primary"
    | "secondary"
    | "success"
    | "danger"
    | "outline"
    | "outline-primary"
    | "outline-secondary";
  size?: "sm" | "md" | "lg";
};

/**
 * A reusable button component with customizable styles.
 *
 * @param {string} [variant="primary"] - The variant of the button.
 *   Supported values are "primary", "secondary", "success", "danger", "outline", "outline-primary", "outline-secondary".
 * @param {string} [size="md"] - The size of the button.
 *   Supported values are "sm", "md", "lg".
 * @param {string} [className] - Additional CSS classes to apply to the button.
 * @param {React.ReactNode} children - The content of the button.
 * @param {React.ButtonHTMLAttributes<HTMLButtonElement>} rest - Additional props to pass to the button element.
 *
 * @example
 * <Button variant="primary" size="lg">Click me</Button>
 */
export default function Button({
  variant = "primary",
  size = "md",
  className = "",
  children,
  ...rest
}: ButtonProps) {
  const base = "btn";

  const variantClass =
    variant === "outline"
      ? "btn-outline-secondary"
      : variant.startsWith("outline-")
      ? `btn-${variant}`
      : `btn-${variant}`;

  const padding =
    size === "sm"
      ? "0 0.5rem" 
      : size === "lg"
      ? "0 1.25rem"
      : "0 1rem";

  return (
    <button
      className={[
        base,
        variantClass,
        "rounded-3 fw-semibold shadow-sm",
        className,
      ]
        .filter(Boolean)
        .join(" ")}
      style={{
        height: "42px", 
        display: "inline-flex",
        alignItems: "center",
        justifyContent: "center",
        padding,
        whiteSpace: "nowrap",
      }}
      {...rest}
    >
      {children}
    </button>
  );
}
