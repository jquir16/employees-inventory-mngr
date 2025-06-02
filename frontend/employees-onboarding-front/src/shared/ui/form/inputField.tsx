type InputFieldProps = React.InputHTMLAttributes<HTMLInputElement> & {
    id: string
    label: string
    error?: string
    touched?: boolean
}

const InputField: React.FC<InputFieldProps> = ({
    id,
    label,
    error,
    touched,
    ...props
}) => (
    <div>
        <label htmlFor={id} className="block text-sm font-medium text-gray-700">{label}</label>
        <input
            id={id}
            name={id}
            {...props}
            className={`mt-1 block w-full px-3 py-2 border ${error && touched ? 'border-red-300' : 'border-gray-300'} rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500`}
        />
        {error && touched && (
            <p className="mt-1 text-sm text-red-600">{error}</p>
        )}
    </div>
)

export default InputField