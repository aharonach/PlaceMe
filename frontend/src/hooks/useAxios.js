import { useState, useEffect } from "react";
import Api from "../api";

const useAxios = (afterSubmit = null, defaultState) => {
    const [response, setResponse] = useState(defaultState);
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const [controller, setController] = useState(null);

    const axiosFetch = async (configObj) => {
        const {
            axiosInstance = Api,
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
            // return err?.response?.data ?? err;
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