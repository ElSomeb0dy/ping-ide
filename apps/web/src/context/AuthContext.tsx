import { createContext, useCallback, useContext, useEffect, useMemo, useReducer, type ReactNode } from "react";
import { login as loginRequest } from "../api/auth";
import { clearAuthToken, getAuthToken, saveAuthToken, setUnauthorizedHandler } from "../api/client";

interface AuthUser {
  id: string;
  roles: string[];
}

interface AuthState {
  token: string | null;
  user: AuthUser | null;
}

type AuthAction =
  | { type: "login"; token: string; user: AuthUser }
  | { type: "logout" };

interface JwtPayload {
  sub?: string;
  groups?: string[];
}

interface AuthContextValue extends AuthState {
  isAuthenticated: boolean;
  isAdmin: boolean;
  login: (loginValue: string, password: string) => Promise<void>;
  loginWithToken: (token: string) => void;
  logout: () => void;
}

const AuthContext = createContext<AuthContextValue | null>(null);

function decodeJwtPayload(token: string): JwtPayload {
  const payload = token.split(".")[1];
  if (!payload) {
    return {};
  }

  return JSON.parse(atob(payload.replace(/-/g, "+").replace(/_/g, "/"))) as JwtPayload;
}

function userFromToken(token: string): AuthUser | null {
  try {
    const payload = decodeJwtPayload(token);
    if (!payload.sub) {
      return null;
    }
    return {
      id: payload.sub,
      roles: payload.groups ?? [],
    };
  } catch {
    return null;
  }
}

function authReducer(state: AuthState, action: AuthAction): AuthState {
  switch (action.type) {
    case "login":
      return { token: action.token, user: action.user };
    case "logout":
      return { token: null, user: null };
    default:
      return state;
  }
}

function initialState(): AuthState {
  const token = getAuthToken();
  const user = token ? userFromToken(token) : null;
  return user && token ? { token, user } : { token: null, user: null };
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [state, dispatch] = useReducer(authReducer, undefined, initialState);

  const logout = () => {
    clearAuthToken();
    dispatch({ type: "logout" });
  };

  const loginWithToken = useCallback((token: string) => {
    const user = userFromToken(token);
    if (!user) {
      throw new Error("Invalid token received from the API");
    }
    saveAuthToken(token);
    dispatch({ type: "login", token, user });
  }, []);

  useEffect(() => {
    setUnauthorizedHandler(logout);
    return () => setUnauthorizedHandler(null);
  }, []);

  const value = useMemo<AuthContextValue>(() => ({
    ...state,
    isAuthenticated: Boolean(state.token && state.user),
    isAdmin: Boolean(state.user?.roles.includes("admin")),
    login: async (loginValue: string, password: string) => {
      const response = await loginRequest(loginValue, password);
      loginWithToken(response.token);
    },
    loginWithToken,
    logout,
  }), [loginWithToken, state]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used inside AuthProvider");
  }
  return context;
}
