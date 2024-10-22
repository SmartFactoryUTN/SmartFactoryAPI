/**
 * Handler that will be called during the execution of a PostUserRegistration flow.
 *
 * @param {Event} event - Details about the context and user that has registered.
 * @param {PostUserRegistrationAPI} api - Methods and utilities to help change the behavior after a signup.
 */
exports.onExecutePostUserRegistration = async (event, api) => {
    // Smartfactory API endpoint
    const apiEndpoint = event.secrets.API_ENDPOINT;
    // Auth0 token endpoint and credentials
    const tokenEndpoint = `https://${event.secrets.AUTH0_DOMAIN}/oauth/token`;
    const clientId = event.secrets.CLIENT_ID;
    const clientSecret = event.secrets.CLIENT_SECRET;
    const audience = event.secrets.API_AUDIENCE;

    // Extract user information from the event object
    const userData = {
        userId: event.user.user_id,
        email: event.user.email,
        name: event.user.name || event.user.nickname,
    };

    try {
        // Generate an access token
        const tokenResponse = await fetch(tokenEndpoint, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                client_id: clientId,
                client_secret: clientSecret,
                audience: audience,
                grant_type: 'client_credentials',
            }),
        });

        if (!tokenResponse.ok) {
            throw new Error(`Failed to obtain access token: ${tokenResponse.statusText}`);
        }

        const tokenData = await tokenResponse.json();
        const accessToken = tokenData.access_token;

        // Send a POST request to Smartfactory API
        const response = await fetch(apiEndpoint, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${accessToken}` // Use the generated access token
            },
            body: JSON.stringify(userData) // Convert userData to JSON
        });

        if (!response.ok) {
            // Handle response error
            console.log(`API call failed: ${response.statusText}`);
        } else {
            // Optional: log success
            console.log('User registration data successfully sent to API');
        }
    } catch (error) {
        // Handle any network errors
        console.error(`Error calling API: ${error.message}`);
    }

};
