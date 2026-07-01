import { useRef, useState } from "react";

export default function CodeEditor({
  initialCode,
  onChange,
}: {
  initialCode: string;
  onChange?: (code: string) => void;
}) {
  const [code, setCode] = useState(initialCode);
  const lineNumbersRef = useRef<HTMLDivElement>(null);
  const textareaRef = useRef<HTMLTextAreaElement>(null);

  const lines = code.split("\n").length;

  const syncScroll = () => {
    if (lineNumbersRef.current && textareaRef.current) {
      lineNumbersRef.current.scrollTop = textareaRef.current.scrollTop;
    }
  };

  return (
    <div className="flex h-full overflow-hidden rounded-xl bg-(--color-surface-elevated)">
      <div
        ref={lineNumbersRef}
        className="select-none overflow-hidden px-3 py-4 text-right font-mono text-sm leading-6 text-(--color-text-mute)"
      >
        {Array.from({ length: lines }, (_, i) => (
          <div key={i}>{i + 1}</div>
        ))}
      </div>
      <textarea
        ref={textareaRef}
        value={code}
        onChange={(e) => {
          setCode(e.target.value);
          onChange?.(e.target.value);
        }}
        onScroll={syncScroll}
        spellCheck={false}
        className="h-full w-full resize-none bg-transparent py-4 pr-4 font-mono text-sm leading-6 text-(--color-text) outline-none"
      />
    </div>
  );
}
