import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  source: '/:path*',
  Headers: [
    {
      key: 'x-middleware-request-headers',
      value: 'cookie',
    },
  ],
}

export default nextConfig;
