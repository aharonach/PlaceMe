import React, {useEffect} from 'react';
import {useParams} from "react-router-dom";
import useAxios from "../../hooks/useAxios";
import Api from '../../api';
import { Alert } from 'react-bootstrap';
import RecordDetails from "../RecordDetails";
import Loading from "../Loading";
import PlacementResultData from "./PlacementResultData";

export default function PlacementResultPage(){

    const [placementResult, error, loading, axiosFetch] = useAxios();
    const { placementId } = useParams();
    const { resultId } = useParams();

    const getPlacementResult = () => {
        axiosFetch({
            axiosInstance: Api,
            method: 'get',
            url: `/placements/${placementId}/results/${resultId}`,
        });
    }

    const details = placementResult && [
        { label: "Name", value: placementResult.name },
        { label: "Description", value: placementResult.description },
        { label: "Created Time", value: placementResult.createdTime },
        { label: "Is Selected Result", value: String(placementResult.selected) },
        { label: "Number Of Classes", value: placementResult.numberOfClasses },
        { label: "Score", value: placementResult.placementScore },
    ];


    useEffect(() => {
        getPlacementResult();
    }, []);

    return (
        <>
            {loading && <Loading />}
            {!loading && error && <Alert variant="danger">{error}</Alert>}
            {!loading && placementResult &&
                <div>
                    <RecordDetails numOfColumns={3} details={details} />
                    <PlacementResultData placementId={placementId} placementResult={placementResult} />
                </div>
            }
        </>
    );
}