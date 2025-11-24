type Option = {
  value: string | number;
  label: string;
};

interface SelectProps {
  options: Option[];
  value: any;
  onChange: (value: any) => void;
  placeholder?: string;
  className?: string;
}

/**
 * A reusable select component with customizable styles.
 *
 * @param {Option[]} options - An array of options to render in the select element.
 * @param {any} value - The current value of the select element.
 * @param {(value: any) => void} onChange - The callback to call when the value of the select element changes.
 * @param {string} [placeholder] - The placeholder text to render in the select element if no value is provided.
 * @param {string} [className] - Additional CSS classes to apply to the select element.
 */
export default function Select({
  options,
  value,
  onChange,
  placeholder,
  className = "",
}: SelectProps) {
  return (
    <select
      className={[
        "form-select",
        "rounded-3",
        "shadow-sm",
        "border-1",
        className,
      ]
        .filter(Boolean)
        .join(" ")}
      style={{ height: "42px", width: "70%" }}
      value={value ?? ""}
      onChange={(e) => {
        const v = e.target.value;
        onChange(v === "" ? "" : isNaN(Number(v)) ? v : Number(v));
      }}
    >
      {placeholder && <option value="">{placeholder}</option>}

      {options.map((o) => (
        <option key={String(o.value)} value={o.value}>
          {o.label}
        </option>
      ))}
    </select>
  );
}
