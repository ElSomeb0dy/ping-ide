import { Outlet } from "react-router-dom";
import Sidebar from "./Sidebar";

export default function Layout() {
  return (
    <div className="flex h-screen overflow-hidden bg-(--color-bg)">
      <Sidebar />
      <main className="h-screen min-w-0 flex-1 overflow-y-auto px-4 py-6 pt-20 sm:px-8 sm:py-8 lg:px-12 lg:py-10 lg:pt-10">
        <div className="mx-auto max-w-6xl">
          <Outlet />
        </div>
      </main>
    </div>
  );
}
