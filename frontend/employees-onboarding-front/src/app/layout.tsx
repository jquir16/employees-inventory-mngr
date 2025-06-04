import type { Metadata } from "next";
import { Inter } from 'next/font/google'
import { Toaster } from "react-hot-toast";
import ReactQueryProvider from "./provider";
import { ReactQueryDevtools } from '@tanstack/react-query-devtools'

import "./globals.css";

const inter = Inter({ subsets: ['latin'] })

export const metadata: Metadata = {
  title: 'Gestión de Colaboradores | CoE Desarrollo',
  description: 'Sistema de gestión de nuevos colaboradores',
};

export default function ProtectedLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
      <html lang="es">
        <body className={inter.className}>
          <ReactQueryProvider>
            {children}
            <Toaster position="bottom-right" />
            <ReactQueryDevtools initialIsOpen={false} />
          </ReactQueryProvider>
        </body>
      </html>
  );
}
