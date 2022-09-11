import {useEffect} from 'react';
import useAxios from "./useAxios";

const useFetchRecord = ({ fetchUrl, thenCallback, dependencies }) => {
    const [response, error, loading, axiosFetch] = useAxios();

    const getRecord = () => {
        axiosFetch({
            method: 'get',
            url: fetchUrl,
        }).then(res => {
            thenCallback && thenCallback(res);
        });
    }

    useEffect(() => {
        getRecord();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, dependencies ?? []);

    return [response, error, loading, axiosFetch, getRecord];
}

export default useFetchRecord;