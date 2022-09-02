import {useEffect} from 'react';
import useAxios from "./useAxios";

const useFetchRecord = ({ fetchUrl, thenCallback, displayFields}) => {
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
    }, []);

    return [response, error, loading, axiosFetch];
}

export default useFetchRecord;