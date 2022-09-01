import {useContext, useEffect} from 'react';
import useAxios from "./useAxios";
import {useLocation} from "react-router-dom";
import RecordContext from "../context/RecordContext";

const useFetchRecord = ({ fetchUrl, thenCallback, displayFields, updateContext = true }) => {
    // const { setRecord } = useContext(RecordContext);
    const [response, error, loading, axiosFetch] = useAxios();
    // let location = useLocation();

    const getRecord = () => {
        axiosFetch({
            method: 'get',
            url: fetchUrl,
        }).then(res => {
            thenCallback && thenCallback(res);
            // updateContext && res && setRecord({ record: res, displayFields: displayFields, pathname: location.pathname });
        });
    }

    useEffect(() => {
        getRecord();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    return [response, error, loading, axiosFetch];
}

export default useFetchRecord;