import { useEffect } from 'react';
import useAxios from "./useAxios";
import Api from "../api";

const useFetchRecord = (fetchUrl, thenCallback) => {
    const [response, error, loading, axiosFetch] = useAxios();

    const getRecord = () => {
        axiosFetch({
            axiosInstance: Api,
            method: 'get',
            url: fetchUrl,
        }).then(res => thenCallback && thenCallback(res));
    }

    useEffect(() => {
        getRecord();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    return [response, error, loading, axiosFetch];
}

export default useFetchRecord;