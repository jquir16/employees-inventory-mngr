import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  source: '/:path*',
  Headers: [
    {
      key: 'x-middleware-request-headers',
      value: 'cookie',
    },
  ],
  reactStrictMode: true,
  swcMinify: true,
  images: {
    domains: ['your-aws-s3-bucket-url.com'],
  },
  async rewrites() {
    return [
      {
        source: '/api/:path*',
        destination: process.env.NEXT_PUBLIC_API_URL + '/:path*',
      },
    ]
  },
  eslint: {
    ignoreDuringBuilds: true,
  },
  typescript: {
    ignoreBuildErrors: true,
  },
  compiler: {
    styledComponents: true,
  },
  experimental: {
    scrollRestoration: true,
  },
}

export default nextConfig;
