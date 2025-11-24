import React from "react";

interface CardProps {
  title?: string;
  children: React.ReactNode;
  className?: string;
}

/**
 * A reusable card component with customizable styles.
 *
 * @param {string} [title] - The title of the card.
 * @param {React.ReactNode} children - The content of the card.
 * @param {string} [className] - Additional CSS classes to apply to the card.
 */
export default function Card({ title, children, className = "" }: CardProps) {
  return (
    <div
      className={[
        "p-3",
        "rounded-3",
        "shadow-sm",
        "bg-white",
        "border",
        "border-light",
        "mw-card",
        className,
      ]
        .filter(Boolean)
        .join(" ")}
      style={{
        borderRadius: "12px",
      }}
    >
      {title && (
        <div className="fw-semibold mb-2" style={{ fontSize: "1rem" }}>
          {title}
        </div>
      )}

      {children}
    </div>
  );
}
