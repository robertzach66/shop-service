import {} from 'angular-auth-oicd-client';

export const authConfig: OPassedInitialConfig = {
    config: {
        authority: "http://localhost:8181/realms/shop-security-realm/protocol/openid-connect/token",
        redirectUrl: window.location.origin,
        postLogoutRedirectUrl: window.location.origin,
        clientId: 'shop-client',
        scope: 'openid profile offline_access',
        responseType: 'code',
        silentRenew: true,
        useRefreshToken: true,
        renewTimeBeforeTokenExpiresInSeconds: 30,
      }
    }
