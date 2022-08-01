import { useState, useEffect } from "react";

const useAxios = () => {
    const [response, setResponse] = useState(null);
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const [controller, setController] = useState();

    const axiosFetch = async (configObj) => {
        const {
            axiosInstance,
            method,
            url,
            data = null,
            requestConfig = {}
        } = configObj;

        try {
            setLoading(true);
            const ctrl = new AbortController();
            setController(ctrl);
            const res = await axiosInstance({
                method: method.toLowerCase(),
                url: url,
                data: data,
                config: {
                    ...requestConfig,
                    signal: ctrl.signal
                }
            });
            // console.log(res);
            setResponse(res.data);
        } catch (err) {
            setError(err?.response?.data?.message ? err.response.data.message : err.message);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        // useEffect cleanup function
        return () => controller && controller.abort();

    }, [controller]);

    return [response, error, loading, axiosFetch];
}

export default useAxios;