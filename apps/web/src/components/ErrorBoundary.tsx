import { Component, type ErrorInfo, type ReactNode } from "react";

export default class ErrorBoundary extends Component<{ children: ReactNode }, { error: Error | null }> {
  constructor(props: { children: ReactNode }) {
    super(props);
    this.state = { error: null };
  }

  static getDerivedStateFromError(error: Error) {
    return { error };
  }

  componentDidCatch(error: Error, info: ErrorInfo) {
    console.error("Unhandled UI error:", error, info.componentStack);
  }

  render() {
    if (this.state.error) {
      return (
        <div className="grid h-screen place-items-center bg-(--color-bg) px-6 text-center">
          <div>
            <h1 className="font-display text-2xl font-bold text-(--color-text)">
              Une erreur est survenue
            </h1>
            <p className="mt-2 text-sm text-(--color-text-soft)">
              Recharge la page pour réessayer.
            </p>
            <button
              onClick={() => window.location.reload()}
              className="mt-5 rounded-full border border-(--color-green) px-5 py-2 text-sm font-medium text-(--color-green) hover:bg-(--color-green-soft)"
            >
              Recharger
            </button>
          </div>
        </div>
      );
    }

    return this.props.children;
  }
}
