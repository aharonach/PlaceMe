import { useState, useEffect } from "react";

const useAxios = (afterSubmit = null) => {
    const [response, setResponse] = useState();
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

            // perform external action
            if ( afterSubmit ) {
                afterSubmit(res);
            } else {
                setResponse(res.data);
            }

            return res.data;
        } catch (err) {
            setError(err?.response?.data?.message ? err.response.data.message : err.message);
            return err?.response?.data ?? err;
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