import React from "react";

interface PageHeaderProps {
  title: string;
  right?: React.ReactNode;
  className?: string;
}

/**
 * A reusable page header component with customizable styles.
 *
 * @param {string} title - The title of the page.
 * @param {React.ReactNode} right - The actions (buttons, filters, etc.) to display on the right side of the header.
 * @param {string} [className] - Additional CSS classes to apply to the header.
 */
export default function PageHeader({
  title,
  right,
  className = "",
}: PageHeaderProps) {
  return (
    <div
      className={[
        "d-flex",
        "justify-content-between",
        "align-items-center",
        "mb-3",
        className,
      ]
        .filter(Boolean)
        .join(" ")}
    >
      <h3 className="fw-bold m-0" style={{ fontSize: "1.35rem" }}>
        {title}
      </h3>

      {right && <div className="ms-2">{right}</div>}
    </div>
  );
}
