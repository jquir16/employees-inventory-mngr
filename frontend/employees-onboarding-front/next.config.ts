import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  reactStrictMode: true,
  swcMinify: true,
  images: {
    domains: ['your-aws-s3-bucket-url.com'],
  },
  async headers() {
    return [
      {
        source: '/:path*',
        headers: [
          {
            key: 'x-middleware-request-headers',
            value: 'cookie',
          },
        ],
      },
    ]
  },
  async rewrites() {
    if (!process.env.NEXT_PUBLIC_API_URL) {
      console.warn('NEXT_PUBLIC_API_URL is not set, rewrites will be disabled');
      return [];
    }
    
    return [
      {
        source: '/api/:path*',
        destination: `${process.env.NEXT_PUBLIC_API_URL}/api/:path*`,
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